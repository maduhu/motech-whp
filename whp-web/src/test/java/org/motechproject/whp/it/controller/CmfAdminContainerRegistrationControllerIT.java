package org.motechproject.whp.it.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.security.authentication.LoginSuccessHandler;
import org.motechproject.security.domain.MotechWebUser;
import org.motechproject.security.exceptions.WebSecurityException;
import org.motechproject.security.service.MotechUser;
import org.motechproject.whp.common.domain.ChannelId;
import org.motechproject.whp.common.domain.District;
import org.motechproject.whp.common.domain.Gender;
import org.motechproject.whp.common.domain.RegistrationInstance;
import org.motechproject.whp.common.repository.AllDistricts;
import org.motechproject.whp.common.service.RemediProperties;
import org.motechproject.whp.common.util.SpringIntegrationTest;
import org.motechproject.whp.container.builder.request.ContainerRegistrationReportingRequestBuilder;
import org.motechproject.whp.container.domain.ContainerRegistrationMode;
import org.motechproject.whp.container.domain.Container;
import org.motechproject.whp.container.domain.ContainerId;
import org.motechproject.whp.container.service.ContainerService;
import org.motechproject.whp.containermapping.domain.AdminContainerMapping;
import org.motechproject.whp.containermapping.domain.ContainerRange;
import org.motechproject.whp.containermapping.domain.ProviderContainerMapping;
import org.motechproject.whp.containermapping.repository.AllAdminContainerMappings;
import org.motechproject.whp.containermapping.repository.AllProviderContainerMappings;
import org.motechproject.whp.controller.CmfAdminContainerRegistrationController;
import org.motechproject.whp.reporting.ReportingApplicationURLs;
import org.motechproject.whp.reports.contract.ContainerRegistrationReportingRequest;
import org.motechproject.whp.user.domain.CmfAdmin;
import org.motechproject.whp.user.domain.WHPRole;
import org.motechproject.whp.user.service.CmfAdminService;
import org.motechproject.whp.user.service.ProviderService;
import org.motechproject.whp.webservice.builder.ProviderRequestBuilder;
import org.motechproject.whp.webservice.request.ProviderWebRequest;
import org.motechproject.whp.webservice.service.ProviderWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.motechproject.whp.common.util.WHPDate.DATE_TIME_FORMAT;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:META-INF/spring/applicationContext.xml")
public class CmfAdminContainerRegistrationControllerIT  extends SpringIntegrationTest {

    @Autowired
    private CmfAdminContainerRegistrationController containerRegistrationController;
    @Autowired
    AllProviderContainerMappings allProviderContainerMappings;
    @Autowired
    AllAdminContainerMappings allAdminContainerMappings;
    @Autowired
    private ProviderWebService providerWebService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private CmfAdminService cmfAdminService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private RemediProperties remediProperties;
    @Autowired
    private ReportingApplicationURLs reportingApplicationURLs;
    @Autowired
    private AllDistricts allDistricts;

    @ReplaceWithMock
    @Autowired
    private HttpClientService httpClientService;
    private final String providerId = "provider";
    private String remediUrl;
    private String apiKey;
    private District district;

    @Before
    public void setUp() throws WebSecurityException {
        remediUrl = remediProperties.getUrl();
        apiKey = remediProperties.getApiKey();
        ProviderContainerMapping providerContainerMapping = new ProviderContainerMapping();
        providerContainerMapping.add(new ContainerRange(10000L, 20000L));
        providerContainerMapping.setProviderId(providerId);
        allProviderContainerMappings.add(providerContainerMapping);

        AdminContainerMapping adminContainerMapping = new AdminContainerMapping();
        adminContainerMapping.add(new ContainerRange(12345678900L, 12345678909L));
        allAdminContainerMappings.removeAll();
        allAdminContainerMappings.add(adminContainerMapping);

        district = new District("Patna");
        allDistricts.add(district);
        ProviderWebRequest whpProviderWeb = new ProviderRequestBuilder().withDefaults().withProviderId(providerId).build();
        providerWebService.createOrUpdate(whpProviderWeb);

        CmfAdmin admin = new CmfAdmin("admin", "password", "test", "Delhi", "Cmf Admin1");
        cmfAdminService.add(admin, "password");

        markForDeletion(providerContainerMapping);
        markForDeletion(adminContainerMapping);
        markForDeletion(providerService.findByProviderId(providerId));
        markForDeletion(admin);

        reset(httpClientService);
    }

    @Test
    public void shouldRegisterTheContainerAndInvokeRemediWithAppropriateXml_OnBehalfOfProvider() throws Exception {
        String containerId = "10000";
        String name = "p1";
        String age = "99";
        String gender = Gender.M.name();
        RegistrationInstance inTreatment = RegistrationInstance.InTreatment;

        ArrayList<String> roles = new ArrayList<>();
        roles.add(WHPRole.CMF_ADMIN.name());

        MotechUser testuser = new MotechUser(new MotechWebUser("testUser", null, null, roles));
        standaloneSetup(containerRegistrationController).build()
                .perform(post("/containerRegistration/by_cmfAdmin/register")
                        .param("containerId", containerId)
                        .param("patientName", name)
                        .param("age", age)
                        .param("gender", gender)
                        .param("instance", inTreatment.getDisplayText()).param("providerId", providerId)
                        .param("containerRegistrationMode", ContainerRegistrationMode.ON_BEHALF_OF_PROVIDER.name())
                        .sessionAttr(LoginSuccessHandler.LOGGED_IN_USER, testuser))
                .andExpect(status().isOk());

        String containerIdValue = new ContainerId(providerId, containerId, ContainerRegistrationMode.ON_BEHALF_OF_PROVIDER).value();
        Container container = containerService.getContainer(containerIdValue);

        assertNotNull(container);
        assertThat(container.getProviderId(), is(providerId));
        assertThat(container.getContainerId(), is(containerIdValue));

        String expectedContainerRegistrationXML = String.format("<?xml version=\"1.0\"?>\n" +
                "<case xmlns=\"http://openrosa.org/javarosa\" case_id=\"%s\" date_modified=\"%s\" user_id=\"motech\"\n" +
                "      api_key=\"%s\">\n" +
                "    <create>\n" +
                "        <case_type>%s</case_type>\n" +
                "    </create>\n" +
                "    <update>\n" +
                "        <provider_id>%s</provider_id>\n" +
                "    </update>\n" +
                "</case>\n", containerIdValue, container.getCreationTime().toString(DATE_TIME_FORMAT), apiKey, inTreatment.name(), providerId);

        verify(httpClientService).post(remediUrl, expectedContainerRegistrationXML);
        verifyReportingEventPublication(testuser, container);
        markForDeletion(container);
    }

    @Test
    public void shouldRegisterTheContainerAndInvokeRemediWithAppropriateXml_ForNewContainer() throws Exception {
        String containerId = "12345678901";
        RegistrationInstance inTreatment = RegistrationInstance.InTreatment;

        ArrayList<String> roles = new ArrayList<>();
        roles.add(WHPRole.CMF_ADMIN.name());

        MotechUser testuser = new MotechUser(new MotechWebUser("testUser", null, null, roles));
        standaloneSetup(containerRegistrationController).build()
                .perform(post("/containerRegistration/by_cmfAdmin/register")
                        .param("containerId", containerId)
                        .param("instance", inTreatment.getDisplayText()).param("providerId", providerId)
                        .param("containerRegistrationMode", ContainerRegistrationMode.NEW_CONTAINER.name())
                        .sessionAttr(LoginSuccessHandler.LOGGED_IN_USER, testuser))
                .andExpect(status().isOk());

        String containerIdValue = new ContainerId(providerId, containerId, ContainerRegistrationMode.NEW_CONTAINER).value();
        Container container = containerService.getContainer(containerIdValue);

        assertNotNull(container);
        assertThat(container.getProviderId(), is(providerId));
        assertThat(container.getContainerId(), is(containerIdValue));

        String expectedContainerRegistrationXML = String.format("<?xml version=\"1.0\"?>\n" +
                "<case xmlns=\"http://openrosa.org/javarosa\" case_id=\"%s\" date_modified=\"%s\" user_id=\"motech\"\n" +
                "      api_key=\"%s\">\n" +
                "    <create>\n" +
                "        <case_type>%s</case_type>\n" +
                "    </create>\n" +
                "    <update>\n" +
                "        <provider_id>%s</provider_id>\n" +
                "    </update>\n" +
                "</case>\n", containerIdValue, container.getCreationTime().toString(DATE_TIME_FORMAT), apiKey, inTreatment.name(), providerId);

        verify(httpClientService).post(remediUrl, expectedContainerRegistrationXML);

        verifyReportingEventPublication(testuser, container);
        markForDeletion(container);
    }

    private void verifyReportingEventPublication(MotechUser testuser, Container container) {
        ContainerRegistrationReportingRequest expectedContainerRegistrationRequest = new ContainerRegistrationReportingRequestBuilder().forContainer(container).registeredThrough(ChannelId.WEB.name()).withSubmitterId(testuser.getUserName()).withSubmitterRole(WHPRole.CMF_ADMIN.name()).build();
        verify(httpClientService).post(reportingApplicationURLs.getContainerRegistrationURL(), expectedContainerRegistrationRequest);
    }

    @After
    public void tearDown() {
        allDistricts.remove(district);
        verifyNoMoreInteractions(httpClientService);
    }
}