package org.motechproject.whp.patient.it.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.common.util.SpringIntegrationTest;
import org.motechproject.whp.patient.builder.PatientRequestBuilder;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.SmearTestRecord;
import org.motechproject.whp.patient.domain.WeightStatisticsRecord;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.service.PatientService;
import org.motechproject.whp.patient.domain.DiseaseClass;
import org.motechproject.whp.common.domain.SampleInstance;
import org.motechproject.whp.common.domain.SmearTestResult;
import org.motechproject.whp.user.builder.ProviderBuilder;
import org.motechproject.whp.user.repository.AllProviders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.*;

@ContextConfiguration(locations = "classpath*:/applicationPatientContext.xml")
public class TreatmentServiceIT extends SpringIntegrationTest {

    private static final String CASE_ID = "TestCaseId";
    private static final String TB_ID = "tb_id";

    @Autowired
    private AllPatients allPatients;
    @Autowired
    private PatientService patientService;
    @Autowired
    AllProviders allProviders;

    private String providerId = "provider-id";

    @Before
    public void setup() {
        createProvider(providerId, "district");
        createTestPatient();
    }

    private void createProvider(String providerId, String district) {
        allProviders.add(new ProviderBuilder()
                .withDefaults()
                .withProviderId(providerId)
                .withDistrict(district)
                .build());
    }

    private void createTestPatient() {
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withProviderId(providerId)
                .withCaseId(CASE_ID)
                .withLastModifiedDate(DateUtil.newDateTime(1990, 3, 17, 4, 55, 50))
                .withPatientAge(50)
                .withTbId(TB_ID)
                .build();
        patientService.createPatient(patientRequest);
    }

    @Test
    public void shouldCreateActiveTreatmentForPatientByDefault() {
        Patient updatedPatient = allPatients.findByPatientId(CASE_ID);
        assertTrue(updatedPatient.isOnActiveTreatment());
    }

    @Test
    public void shouldCreateActiveTreatmentForPatientOnOpen() {
        PatientRequest updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForCloseTreatment()
                .withProviderId(providerId)
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();

        patientService.update(updatePatientRequest);

        updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForOpenNewTreatment()
                .withProviderId(providerId)
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();

        patientService.update(updatePatientRequest);

        Patient updatedPatient = allPatients.findByPatientId(CASE_ID);
        assertTrue(updatedPatient.isOnActiveTreatment());
    }

    @Test
    public void shouldMarkPatientAsNotHavingActiveTreatmentOnClose() {
        PatientRequest updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForCloseTreatment()
                .withProviderId(providerId)
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();

        patientService.update(updatePatientRequest);

        Patient updatedPatient = allPatients.findByPatientId(CASE_ID);
        assertFalse(updatedPatient.isOnActiveTreatment());
    }

    @Test
    public void shouldMarkPatientAsHavingActiveTreatmentOnTransferIn() {
        PatientRequest updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForCloseTreatment()
                .withProviderId(providerId)
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();
        patientService.update(updatePatientRequest);

        String newProviderId = "new-provider-id";
        createProvider(newProviderId, "newDistrict");

        updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForTransferInTreatment()
                .withProviderId(newProviderId)
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();

        patientService.update(updatePatientRequest);

        Patient updatedPatient = allPatients.findByPatientId(CASE_ID);
        assertTrue(updatedPatient.isOnActiveTreatment());
    }

    @Test
    public void shouldUpdateDiseaseClassOnTransferIn() {
        PatientRequest updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForCloseTreatment()
                .withProviderId(providerId)
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();

        patientService.update(updatePatientRequest);

        Patient updatedPatient = allPatients.findByPatientId(CASE_ID);
        assertEquals(DiseaseClass.P, updatedPatient.getCurrentTherapy().getDiseaseClass());

        updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForTransferInTreatment()
                .withProviderId(providerId)
                .withDiseaseClass(DiseaseClass.E)
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();

        patientService.update(updatePatientRequest);

        updatedPatient = allPatients.findByPatientId(CASE_ID);
        assertEquals(DiseaseClass.E, updatedPatient.getCurrentTherapy().getDiseaseClass());
    }

    @Test
    public void shouldCaptureNewSmearTestResultsAndWeightStatisticsIfSent() {
        PatientRequest updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForCloseTreatment()
                .withProviderId(providerId)
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();
        patientService.update(updatePatientRequest);

        updatePatientRequest = new PatientRequestBuilder()
                .withMandatoryFieldsForTransferInTreatment()
                .withProviderId(providerId)
                .withSmearTestResults(SampleInstance.PreTreatment, DateUtil.newDate(2012, 5, 19), SmearTestResult.Positive, DateUtil.newDate(2012, 5, 19), SmearTestResult.Positive)
                .withWeightStatistics(SampleInstance.PreTreatment, 30.00, DateUtil.newDate(2012, 5, 19))
                .withCaseId(CASE_ID)
                .withTbId(TB_ID)
                .build();
        patientService.update(updatePatientRequest);

        Patient updatedPatient = allPatients.findByPatientId(CASE_ID);
        SmearTestRecord smearTestRecord = updatedPatient.getSmearTestResults().get(0);
        WeightStatisticsRecord weightStatisticsRecord = updatedPatient.getWeightStatistics().get(0);
        assertEquals(SampleInstance.PreTreatment, smearTestRecord.getSmear_sample_instance());
        assertEquals(DateUtil.newDate(2012, 5, 19), smearTestRecord.getSmear_test_date_1());
        assertEquals(SmearTestResult.Positive, smearTestRecord.getSmear_test_result_1());
        assertEquals(DateUtil.newDate(2012, 5, 19), smearTestRecord.getSmear_test_date_2());
        assertEquals(SmearTestResult.Positive, smearTestRecord.getSmear_test_result_2());

        assertEquals(SampleInstance.PreTreatment, weightStatisticsRecord.getWeight_instance());
        assertEquals(DateUtil.newDate(2012, 5, 19), weightStatisticsRecord.getMeasuringDate());
        assertEquals(30.00, weightStatisticsRecord.getWeight());
    }
//
//    @Test
//    public void shouldTransferInPatient_WhenMinimumRequiredFieldsAreSent() {
//
//        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults().build();
//        patientService.createPatient(patientRequest);
//
//        Patient patient = allPatients.findByPatientId(patientRequest.getCase_id());
//
//        String newProviderId = "newProviderId";
//        String tbId = "newTbId";
//        DateTime now = now();
//
//        treatmentService.transferInPatient(newProviderId, patient, tbId, "" , now, new SmearTestResults(), new WeightStatistics());
//
//        Patient updatedPatient = allPatients.findByPatientId(patient.getPatientId());
//
//        assertEquals(PatientType.TransferredIn, updatedPatient.getCurrentTreatment().getPatientType());
//
//        assertEquals(newProviderId.toLowerCase(), updatedPatient.getCurrentTreatment().getProviderId());
//        assertEquals(tbId.toLowerCase(), updatedPatient.getCurrentTreatment().getTbId());
//        assertEquals("", updatedPatient.getCurrentTreatment().getTbRegistrationNumber());
//        assertEquals(now.toLocalDate(), updatedPatient.getCurrentTreatment().getStartDate());
//        assertEquals(now, updatedPatient.getLastModifiedDate());
//
//        assertTrue(patient.getCurrentTreatment().getSmearTestResults().isEmpty());
//        assertTrue(patient.getCurrentTreatment().getWeightStatistics().isEmpty());
//    }
//
//    @Test
//    public void shouldTransferInPatient_WithNewlySent_SmearTestResultsAndWeightStatistics() {
//
//        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults().build();
//        patientService.createPatient(patientRequest);
//
//        Patient patient = allPatients.findByPatientId(patientRequest.getCase_id());
//
//        String newProviderId = "newProviderId";
//        String tbId = "newTbId";
//        String newTbRegistrationNumber = "newTbRegistrationNumber";
//        DateTime now = now();
//
//        treatmentService.transferInPatient(newProviderId, patient, tbId, newTbRegistrationNumber, now, patientRequest.getSmearTestResults(), patientRequest.getWeightStatistics());
//
//        Patient updatedPatient = allPatients.findByPatientId(patient.getPatientId());
//
//        assertEquals(newProviderId.toLowerCase(), updatedPatient.getCurrentTreatment().getProviderId());
//        assertEquals(tbId.toLowerCase(), updatedPatient.getCurrentTreatment().getTbId());
//        assertEquals(newTbRegistrationNumber, updatedPatient.getCurrentTreatment().getTbRegistrationNumber());
//        assertEquals(now.toLocalDate(), updatedPatient.getCurrentTreatment().getStartDate());
//        assertEquals(now, updatedPatient.getLastModifiedDate());
//        assertEquals(PatientType.TransferredIn, updatedPatient.getCurrentTreatment().getPatientType());
//
//        assertEquals(1, patient.getCurrentTreatment().getSmearTestResults().size());
//        assertEquals(DateUtil.newDate(2010, 5, 19), patient.getCurrentTreatment().getSmearTestResults().latestResult().getSmear_test_date_1());
//        assertEquals(DateUtil.newDate(2010, 5, 21), patient.getCurrentTreatment().getSmearTestResults().latestResult().getSmear_test_date_2());
//        assertEquals(SmearTestResult.Positive, patient.getCurrentTreatment().getSmearTestResults().latestResult().getSmear_test_result_1());
//        assertEquals(SmearTestResult.Positive, patient.getCurrentTreatment().getSmearTestResults().latestResult().getSmear_test_result_2());
//        assertEquals(DateUtil.newDate(2010, 5, 19), patient.getCurrentTreatment().getWeightStatistics().latestResult().getMeasuringDate());
//        assertEquals(Double.valueOf(99.7), patient.getCurrentTreatment().getWeightStatistics().latestResult().getWeight());
//    }

    @After
    public void tearDown() {
        markForDeletion(allPatients.getAll().toArray());
        allProviders.removeAll();
        super.after();
    }
}
