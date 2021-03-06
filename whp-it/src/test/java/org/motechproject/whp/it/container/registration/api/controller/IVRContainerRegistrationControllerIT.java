package org.motechproject.whp.it.container.registration.api.controller;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.motechproject.http.client.service.HttpClientService;
import org.motechproject.security.exceptions.WebSecurityException;
import org.motechproject.whp.common.domain.ChannelId;
import org.motechproject.whp.common.domain.District;
import org.motechproject.whp.common.domain.SputumTrackingInstance;
import org.motechproject.whp.common.repository.AllDistricts;
import org.motechproject.whp.common.service.RemediProperties;
import org.motechproject.whp.common.util.SpringIntegrationTest;
import org.motechproject.whp.container.builder.request.ContainerRegistrationReportingRequestBuilder;
import org.motechproject.whp.container.domain.Container;
import org.motechproject.whp.container.domain.ContainerId;
import org.motechproject.whp.container.repository.AllContainers;
import org.motechproject.whp.container.service.ContainerService;
import org.motechproject.whp.containermapping.domain.ContainerRange;
import org.motechproject.whp.containermapping.domain.ProviderContainerMapping;
import org.motechproject.whp.containermapping.repository.AllProviderContainerMappings;
import org.motechproject.whp.containerregistration.api.webservice.IVRContainerRegistrationController;
import org.motechproject.whp.reporting.ReportingApplicationURLs;
import org.motechproject.whp.reports.contract.ContainerRegistrationReportingRequest;
import org.motechproject.whp.user.domain.WHPRole;
import org.motechproject.whp.user.service.ProviderService;
import org.motechproject.whp.webservice.builder.ProviderRequestBuilder;
import org.motechproject.whp.webservice.request.ProviderWebRequest;
import org.motechproject.whp.webservice.service.ProviderWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.server.request.MockMvcRequestBuilders;
import org.springframework.test.web.server.result.MockMvcResultMatchers;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.motechproject.whp.common.util.WHPDate.DATE_TIME_FORMAT;
import static org.motechproject.whp.container.domain.ContainerRegistrationMode.ON_BEHALF_OF_PROVIDER;

@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath*:/applicationContainerRegistrationApiContext.xml")
public class IVRContainerRegistrationControllerIT extends SpringIntegrationTest {

    @Autowired
    private IVRContainerRegistrationController IVRContainerRegistrationController;
    @Autowired
    AllProviderContainerMappings allProviderContainerMappings;
    @Autowired
    private ProviderWebService providerWebService;
    @Autowired
    private ProviderService providerService;
    @Autowired
    private ContainerService containerService;
    @Autowired
    private RemediProperties remediProperties;
    @Autowired
    private ReportingApplicationURLs reportingApplicationURLs;
    @Autowired
    private AllDistricts allDistricts;
    @Autowired
    private AllContainers allContainers;

    @ReplaceWithMock
    @Autowired
    private HttpClientService httpClientService;
    private final String providerId = "provider";
    private String containerIdNumber = "12345";
    private String remediUrl;
    private String apiKey;
    private District district;


    @Before
    public void setUp() throws WebSecurityException {
        allContainers.removeAll();

        remediUrl = remediProperties.getUrl();
        apiKey = remediProperties.getApiKey();
        ProviderContainerMapping providerContainerMapping = new ProviderContainerMapping();
        providerContainerMapping.add(new ContainerRange(12344L, 12346L));
        providerContainerMapping.setProviderId(providerId);
        allProviderContainerMappings.add(providerContainerMapping);

        district = new District("Patna");
        allDistricts.add(district);
        String primaryMobile = "0986754322";
        ProviderWebRequest whpProviderWeb = new ProviderRequestBuilder().withDefaults().withProviderId(providerId).withPrimaryMobile(primaryMobile).build();
        providerWebService.createOrUpdate(whpProviderWeb);

        markForDeletion(providerContainerMapping);
        markForDeletion(providerService.findByProviderId(providerId));

        reset(httpClientService);
    }

    @Test
    public void shouldRegisterTheContainer() throws Exception {

        String containerId = new ContainerId(providerId, containerIdNumber, ON_BEHALF_OF_PROVIDER).value();
        SputumTrackingInstance inTreatment = SputumTrackingInstance.PreTreatment;

        MockMvcBuilders.standaloneSetup(IVRContainerRegistrationController)
                .build()
                .perform(
                        MockMvcRequestBuilders.post("/ivr/containerRegistration/register")
                                .body(readXML("/validIVRContainerRegistrationRequest.xml"))
                                .contentType(MediaType.APPLICATION_XML)

                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().string(containsString("success"))
                );

        Container container = containerService.getContainer(containerId);

        assertNotNull(container);
        assertThat(container.getProviderId(), is(providerId));
        assertThat(container.getContainerId(), is(containerId));

        String expectedContainerRegistrationXML = String.format("<?xml version=\"1.0\"?>\n" +
                "<case xmlns=\"http://openrosa.org/javarosa\" case_id=\"%s\" date_modified=\"%s\" user_id=\"motech\"\n" +
                "      api_key=\"%s\">\n" +
                "    <create>\n" +
                "        <case_type>%s</case_type>\n" +
                "    </create>\n" +
                "    <update>\n" +
                "        <provider_id>%s</provider_id>\n" +
                "    </update>\n" +
                "</case>\n", containerId, container.getCreationTime().toString(DATE_TIME_FORMAT), apiKey, inTreatment.name(), providerId);

        verify(httpClientService).post(remediUrl, expectedContainerRegistrationXML);
        markForDeletion(container);
        ContainerRegistrationReportingRequest expectedContainerRegistrationRequest = new ContainerRegistrationReportingRequestBuilder().forContainer(container)
                .registeredThrough(ChannelId.IVR.name())
                .withSubmitterId(providerId)
                .withSubmitterRole(WHPRole.PROVIDER.name())
                .withCallId("64756435684375")
                .build();
        verify(httpClientService).post(reportingApplicationURLs.getContainerRegistrationURL(), expectedContainerRegistrationRequest);
    }

    @After
    public void tearDown() {
        allDistricts.remove(district);
        verifyNoMoreInteractions(httpClientService);
    }

    private byte[] readXML(String xmlPath) throws IOException {
        return IOUtils.toByteArray(this.getClass().getResourceAsStream(xmlPath));
    }
}