package org.motechproject.whp.controller;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.whp.common.domain.ContainerStatus;
import org.motechproject.whp.common.domain.Diagnosis;
import org.motechproject.whp.common.domain.District;
import org.motechproject.whp.common.domain.SmearTestResult;
import org.motechproject.whp.common.repository.AllDistricts;
import org.motechproject.whp.container.contract.ContainerClosureRequest;
import org.motechproject.whp.container.contract.ContainerPatientDetailsRequest;
import org.motechproject.whp.container.domain.AlternateDiagnosis;
import org.motechproject.whp.container.domain.Container;
import org.motechproject.whp.container.domain.ReasonForContainerClosure;
import org.motechproject.whp.container.service.ContainerService;
import org.motechproject.whp.container.service.ContainerTrackingService;
import org.motechproject.whp.container.service.ReasonsForClosureService;
import org.motechproject.whp.container.validation.ReasonForClosureValidator;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.whp.controller.PreTreatmentContainerTrackingController.*;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

public class PreTreatmentContainerTrackingControllerTest {

    PreTreatmentContainerTrackingController containerTrackingController;

    @Mock
    ContainerService containerService;
    @Mock
    ContainerTrackingService containerTrackingService;

    @Mock
    AllDistricts allDistricts;

    @Mock
    ContainerClosureRequest containerClosureRequest;

    @Mock
    Container container;

    @Mock
    private ReasonForClosureValidator reasonForClosureValidator;
    @Mock
    private ReasonsForClosureService reasonsForClosureService;

    @Before
    public void setUp() {
        initMocks(this);
        containerTrackingController = new PreTreatmentContainerTrackingController(containerService, allDistricts, reasonForClosureValidator, reasonsForClosureService);
    }

    @Test
    public void shouldPopulateUIModelForPreTreatmentDashboard() throws Exception {
        ArrayList<ReasonForContainerClosure> reasonsForClosureForm = new ArrayList<>();
        ArrayList<ReasonForContainerClosure> reasonsForFilter = new ArrayList<>();
        ArrayList<AlternateDiagnosis> alternateDiagnosises = new ArrayList<>();
        List<District> districts = asList(new District("D1"), new District("D2"));

        when(reasonsForClosureService.getAllPreTreatmentClosureReasonsForAdmin()).thenReturn(reasonsForClosureForm);
        when(containerService.getAllAlternateDiagnosis()).thenReturn(alternateDiagnosises);
        when(reasonsForClosureService.getAllReasonsPreTreatmentClosureReasons()).thenReturn(reasonsForFilter);
        when(allDistricts.getAll()).thenReturn(districts);

        standaloneSetup(containerTrackingController).build()
                .perform(get("/sputum-tracking/pre-treatment/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().size(7))
                .andExpect(model().attribute(DIAGNOSIS_LIST, Diagnosis.allNames()))
                .andExpect(model().attribute(CONTAINER_STATUS_LIST, ContainerStatus.allNames()))
                .andExpect(model().attribute(LAB_RESULTS, SmearTestResult.allNames()))
                .andExpect(model().attribute(REASONS, reasonsForClosureForm))
                .andExpect(model().attribute(REASONS_FOR_FILTER, reasonsForFilter))
                .andExpect(model().attribute(DISTRICTS, allDistricts.getAll()))
                .andExpect(model().attribute(ALTERNATE_DIAGNOSIS_LIST, alternateDiagnosises));

        verify(reasonsForClosureService).getAllPreTreatmentClosureReasonsForAdmin();
        verify(containerService).getAllAlternateDiagnosis();
    }

    @Test
    public void shouldPopulateErrorsIfReasonForClosureRequestFailsValidation() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("some error");
        when(reasonForClosureValidator.validate(any(ContainerClosureRequest.class))).thenReturn(errors);

        List<ReasonForContainerClosure> reasons = new ArrayList<>();
        List<AlternateDiagnosis> alternateDiagnosis = new ArrayList<>();
        when(reasonsForClosureService.getAllPreTreatmentClosureReasonsForAdmin()).thenReturn(reasons);
        when(containerService.getAllAlternateDiagnosis()).thenReturn(alternateDiagnosis);

        standaloneSetup(containerTrackingController).build()
                .perform(post("/sputum-tracking/pre-treatment/close-container"))
                .andExpect(content().type("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("some")));

        verify(reasonForClosureValidator).validate(any(ContainerClosureRequest.class));
    }

    @Test
    public void shouldCloseContainer() throws Exception {
        when(reasonForClosureValidator.validate(any(ContainerClosureRequest.class))).thenReturn(new ArrayList<String>());

        standaloneSetup(containerTrackingController).build()
                .perform(post("/sputum-tracking/pre-treatment/close-container"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("success")));

        verify(containerService).closeContainer(any(ContainerClosureRequest.class));
    }

    @Test
    public void shouldOpenContainer() throws Exception {
        String containerId = "1234";
        standaloneSetup(containerTrackingController).build()
                .perform(get("/sputum-tracking/pre-treatment/open-container").param("containerId", containerId))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        verify(containerService).openContainer(containerId);
    }

    @Test
    public void shouldUpdatePatientDetails() throws Exception{
        String patientId = "12345";
        String patientName = "patient123";
        String containerId = "1234567890";

        standaloneSetup(containerTrackingController).build()
                .perform(post("/sputum-tracking/pre-treatment/updatePatientDetails").param("containerId", containerId).param("patientName", patientName).param("patientId", patientId))
                .andExpect(status().isOk());

        ArgumentCaptor<ContainerPatientDetailsRequest> captor = ArgumentCaptor.forClass(ContainerPatientDetailsRequest.class);
        verify(containerService).updatePatientDetails(captor.capture());
        ContainerPatientDetailsRequest patientDetailsRequest = captor.getValue();

        assertThat(patientDetailsRequest.getContainerId(), is(containerId));
        assertThat(patientDetailsRequest.getPatientId(), is(patientId));
        assertThat(patientDetailsRequest.getPatientName(), is(patientName));
    }
}
