package org.motechproject.whp.patient.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.builder.PatientRequestBuilder;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.refdata.domain.DiseaseClass;
import org.motechproject.whp.user.service.ProviderService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.whp.patient.builder.PatientBuilder.PATIENT_ID;

public class TreatmentServiceTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private ProviderService providerService;

    private TreatmentService treatmentService;

    @Before
    public void setUp() {
        initMocks(this);
        Patient patient = new PatientBuilder().withDefaults().build();
        when(allPatients.findByPatientId(PATIENT_ID)).thenReturn(patient);

        treatmentService = new TreatmentService(allPatients, null, providerService);
    }

    @Test
    public void shouldOverwriteDiseaseClass_DuringTransferIn() {
        PatientRequest transferInRequest = new PatientRequestBuilder().withMandatoryFieldsForTransferInTreatment().withDiseaseClass(DiseaseClass.E).build();

        treatmentService.transferInPatient(transferInRequest);

        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        verify(allPatients).update(patientArgumentCaptor.capture());
        assertEquals(DiseaseClass.E, patientArgumentCaptor.getValue().currentTherapy().getDiseaseClass());
    }

}