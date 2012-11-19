package org.motechproject.whp.webservice.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.casexml.builder.ResponseMessageBuilder;
import org.motechproject.casexml.service.CaseLogService;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.common.domain.RegistrationInstance;
import org.motechproject.whp.common.exception.WHPErrorCode;
import org.motechproject.whp.common.util.WHPDate;
import org.motechproject.whp.common.validation.RequestValidator;
import org.motechproject.whp.container.domain.Container;
import org.motechproject.whp.container.service.ContainerService;
import org.motechproject.whp.webservice.builder.SputumLabResultsWebRequestBuilder;
import org.motechproject.whp.webservice.mapper.SputumLabResultsMapper;
import org.motechproject.whp.webservice.request.SputumLabResultsWebRequest;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.LocalDate.parse;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.whp.common.util.WHPDate.DATE_FORMAT;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

public class SputumLabResultsWebServiceTest extends BaseWebServiceTest {

    private SputumLabResultsWebService sputumLabResultsWebService;

    @Mock
    private CaseLogService caseLogService;

    @Mock
    private ContainerService containerService;

    @Mock
    private RequestValidator validator;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        sputumLabResultsWebService = spy(new SputumLabResultsWebService(containerService, new SputumLabResultsMapper(), validator));
        sputumLabResultsWebService.setResponseMessageBuilder(mock(ResponseMessageBuilder.class));
        sputumLabResultsWebService.setCaseLogService(caseLogService);
    }

    @Test
    public void shouldConvertCaseXMLToSputumLabResultsWebRequest() throws Exception {
        String containerId = "12651654165465";
        
        String requestBody = "<?xml version=\"1.0\"?>\n" +
                "<case xmlns=\"http://openrosa.org/javarosa\" case_id=\"12651654165465\" date_modified=\"03/04/2012\n" +
                "11:23:40\" user_id=\"system\" api_key=\"3F2504E04F8911D39A0C0305E82C3301\">\n" +
                "<update>\n" +
                "<update_type>lab_results</update_type>\n" +
                "<smear_test_date_1>01/03/2012</smear_test_date_1>\n" +
                "<smear_test_result_1>Positive</smear_test_result_1>\n" +
                "<smear_test_date_2>01/03/2012</smear_test_date_2>\n" +
                "<smear_test_result_2>Positive</smear_test_result_2>\n" +
                "<lab_name>XYZ</lab_name>\n" +
                "<lab_number>1234</lab_number>\n" +
                "</update>\n" +
                "</case>";

        when(containerService.getContainer(containerId)).thenReturn(new Container("providerId", containerId, RegistrationInstance.InTreatment, DateUtil.now(), "d1"));

        standaloneSetup(sputumLabResultsWebService).build()
                .perform(post("/sputumLabResults/process").body(requestBody.getBytes()).contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());

        ArgumentCaptor<SputumLabResultsWebRequest> argumentCaptor = ArgumentCaptor.forClass(SputumLabResultsWebRequest.class);

        verify(sputumLabResultsWebService).updateCase(argumentCaptor.capture());

        SputumLabResultsWebRequest sputumLabResultsWebRequest = argumentCaptor.getValue();
        assertThat(sputumLabResultsWebRequest.getCase_id(), is(containerId));
        assertThat(sputumLabResultsWebRequest.getDate_modified(), is("03/04/2012 11:23:40"));
        assertThat(sputumLabResultsWebRequest.getApi_key(), is("3F2504E04F8911D39A0C0305E82C3301"));
        assertThat(sputumLabResultsWebRequest.getUpdate_type(), is("lab_results"));
        assertThat(sputumLabResultsWebRequest.getSmear_test_date_1(), is("01/03/2012"));
        assertThat(sputumLabResultsWebRequest.getSmear_test_date_2(), is("01/03/2012"));
        assertThat(sputumLabResultsWebRequest.getSmear_test_result_1(), is("Positive"));
        assertThat(sputumLabResultsWebRequest.getSmear_test_result_2(), is("Positive"));
        assertThat(sputumLabResultsWebRequest.getLab_name(), is("XYZ"));
        assertThat(sputumLabResultsWebRequest.getLab_number(), is("1234"));
    }

    @Test
    public void shouldValidateSputumLabResultsWebRequest_forUnknownContainerId(){
        expectWHPCaseException(WHPErrorCode.INVALID_CONTAINER_ID);

        String invalidContainerId = "12651654165465";
        SputumLabResultsWebRequest request = new SputumLabResultsWebRequestBuilder().withCase_id(invalidContainerId)
                .withDate_modified("03/04/2012 11:23:40")
                .withSmear_test_date_1("01/03/2012")
                .withSmear_test_result_1("Positive")
                .withSmear_test_date_2("01/03/2012")
                .withSmear_test_result_2("Positive")
                .withLab_name("XYZ")
                .withLab_number("1234")
                .build();

        when(containerService.getContainer(invalidContainerId)).thenReturn(null);

        sputumLabResultsWebService.updateCase(request);
    }

    @Test
    public void shouldValidateSputumLabResultsWebRequest_forIncompleteLabResults(){

        expectWHPCaseException(WHPErrorCode.SPUTUM_LAB_RESULT_IS_INCOMPLETE);

        String containerId = "12651654165465";
        SputumLabResultsWebRequest request = new SputumLabResultsWebRequestBuilder().withCase_id(containerId)
                .withDate_modified("03/04/2012 11:23:40")
                .withSmear_test_date_1("")
                .withSmear_test_result_1("")
                .withSmear_test_date_2("01/03/2012")
                .withSmear_test_result_2("Positive")
                .withLab_name("XYZ")
                .withLab_number("1234")
                .build();

        when(containerService.getContainer(containerId)).thenReturn(new Container("providerId", containerId, RegistrationInstance.InTreatment, DateUtil.now(), "d1"));

        sputumLabResultsWebService.updateCase(request);
    }


    @Test
    public void shouldUpdateContainerWithLabResults(){
        String containerId = "12651654165465";
        SputumLabResultsWebRequest request = new SputumLabResultsWebRequestBuilder().withCase_id(containerId)
                .withDate_modified("03/04/2012 11:23:40")
                .withSmear_test_date_1("01/03/2012")
                .withSmear_test_result_1("Positive")
                .withSmear_test_date_2("01/03/2012")
                .withSmear_test_result_2("Positive")
                .withLab_name("XYZ")
                .withLab_number("1234")
                .build();

        Container container = new Container("providerId", containerId, RegistrationInstance.InTreatment, DateUtil.now(), "d1");

        when(containerService.getContainer(containerId)).thenReturn(container);

        sputumLabResultsWebService.updateCase(request);

        ArgumentCaptor<Container> containerArgumentCaptor = ArgumentCaptor.forClass(Container.class);
        verify(containerService).updateLabResults(containerArgumentCaptor.capture());

        assertThat(container.getLabResults().getSmearTestDate1(), is(parse(request.getSmear_test_date_1(), forPattern(DATE_FORMAT))));
        assertThat(container.getLabResults().getSmearTestDate2(), is(parse(request.getSmear_test_date_2(), forPattern(DATE_FORMAT))));
        assertThat(container.getLabResults().getSmearTestResult1().value(), is(request.getSmear_test_result_1()));
        assertThat(container.getLabResults().getSmearTestResult2().value(), is(request.getSmear_test_result_2()));
        assertThat(container.getLabResults().getCapturedOn(), is(DateTime.parse(request.getDate_modified(), forPattern(WHPDate.DATE_TIME_FORMAT))));
    }
}
