package org.motechproject.whp.applicationservice.orchestrator.phaseUpdateOrchestrator.part;

import org.mockito.Mock;
import org.motechproject.whp.adherence.service.WHPAdherenceService;
import org.motechproject.whp.applicationservice.orchestrator.PhaseUpdateOrchestrator;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.service.PatientService;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public abstract class PhaseUpdateOrchestratorTestPart {

    public static final String THERAPY_ID = "therapyUid";
    public static final String PATIENT_ID = "patientid";

    @Mock
    protected AllPatients allPatients;
    @Mock
    protected WHPAdherenceService whpAdherenceService;
    @Mock
    protected PatientService patientService;

    protected Patient patient;

    protected PhaseUpdateOrchestrator phaseUpdateOrchestrator;

    public void setUp() {
        initMocks(this);
        phaseUpdateOrchestrator = new PhaseUpdateOrchestrator(allPatients, patientService, whpAdherenceService);
        patient = new PatientBuilder().withDefaults().withPatientId(PATIENT_ID).build();
        patient.getCurrentTherapy().setUid(THERAPY_ID);
        when(allPatients.findByPatientId(PATIENT_ID)).thenReturn(patient);
    }
}