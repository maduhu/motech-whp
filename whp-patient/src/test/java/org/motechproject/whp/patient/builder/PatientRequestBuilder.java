package org.motechproject.whp.patient.builder;

import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.common.domain.Gender;
import org.motechproject.whp.common.domain.SmearTestResult;
import org.motechproject.whp.common.domain.SputumTrackingInstance;
import org.motechproject.whp.common.util.WHPDate;
import org.motechproject.whp.common.util.WHPDateTime;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.contract.TreatmentUpdateScenario;
import org.motechproject.whp.patient.domain.*;

import java.util.Arrays;

import static org.motechproject.util.DateUtil.today;
import static org.motechproject.whp.patient.builder.PatientBuilder.*;

public class PatientRequestBuilder {

    public static final String NEW_TB_ID = "newtbid";
    public static final String NEW_PROVIDER_ID = "newproviderid";
    public static final String TODAY_DATE = WHPDate.date(DateUtil.now().toLocalDate()).value();
    public static final String TODAY_DATE_TIME = WHPDateTime.date(DateUtil.now()).value();
    protected final TreatmentCategory category01 = new TreatmentCategory("RNTCP Category 1", "01", 3, 8, 24, 4, 12, 18, 54, Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday));
    protected final TreatmentCategory category10 = new TreatmentCategory("RNTCP Category 1", "10", 3, 8, 24, 4, 12, 18, 54, Arrays.asList(DayOfWeek.Monday));

    private PatientRequest patientRequest = new PatientRequest();

    public PatientRequest build() {
        return patientRequest;
    }

    public PatientRequestBuilder withDefaults() {
        patientRequest = new PatientRequest()
                .setPatientInfo(PATIENT_ID, "Foo", "Bar", Gender.M, PatientType.Chronic, "1234567890", "phi")
                .setTreatmentData(category01, TB_ID, PROVIDER_ID, DiseaseClass.P, 50, "registrationNumber", "21/06/2010 10:00:05")
                .addSmearTestResults(SputumTrackingInstance.PreTreatment, DateUtil.newDate(2010, 5, 19), SmearTestResult.Positive, DateUtil.newDate(2010, 5, 21), SmearTestResult.Positive, "labName", "labNumber")
                .setPatientAddress("house number", "landmark", "block", "village", "district", "state")
                .setWeightStatistics(SputumTrackingInstance.PreTreatment, 99.7, DateUtil.newDate(2010, 5, 19));

        patientRequest.setDate_modified("17/03/1990 04:55:00");
        patientRequest.setTb_registration_date("17/03/1990");
        patientRequest.setDate_of_birth("17/03/1981");
        return this;
    }

    public PatientRequestBuilder withSimpleUpdateFields() {
        patientRequest = new PatientRequest()
                .setPatientInfo(PATIENT_ID, null, null, null, null, "9087654321", null)
                .setPatientAddress("new_house number", "new_landmark", "new_block", "new_village", "new_district", "new_state")
                .addSmearTestResults(SputumTrackingInstance.EndTreatment, DateUtil.newDate(2010, 7, 19), SmearTestResult.Negative, DateUtil.newDate(2010, 9, 20), SmearTestResult.Negative, "labName", "labNumber")
                .setWeightStatistics(SputumTrackingInstance.EndTreatment, 99.7, DateUtil.newDate(2010, 9, 20))
                .setTreatmentData(null, TB_ID, null, null, 50, "newRegistrationNumber", "20/9/2010 10:10:0");

        patientRequest.setTb_registration_date(TODAY_DATE);
        return this;
    }

    public PatientRequestBuilder withMandatoryFieldsForOpenNewTreatment() {
        patientRequest.setCase_id(PATIENT_ID);
        patientRequest.setDate_modified(TODAY_DATE_TIME);
        patientRequest.setTb_registration_date(TODAY_DATE);
        patientRequest.setTb_id("tbid");
        patientRequest.setTreatment_category(category10);
        patientRequest.setProvider_id("newproviderid");
        patientRequest.setDisease_class(DiseaseClass.E);
        patientRequest.setTreatmentUpdate(TreatmentUpdateScenario.New);
        patientRequest.addSmearTestResults(SputumTrackingInstance.EndIP, today(), SmearTestResult.Negative, today(), SmearTestResult.Negative, "labName", "labNumber");
        patientRequest.setWeightStatistics(SputumTrackingInstance.EndIP, 67.56, WHPDateTime.date(patientRequest.getDate_modified()).dateTime().toLocalDate());

        return this;
    }

    public PatientRequestBuilder withMandatoryFieldsForCloseTreatment() {
        patientRequest.setCase_id(PATIENT_ID);
        patientRequest.setDate_modified(TODAY_DATE_TIME);
        patientRequest.setTb_id(TB_ID);
        patientRequest.setTreatmentUpdate(TreatmentUpdateScenario.Close);
        patientRequest.setTreatment_outcome(TreatmentOutcome.Cured);
        patientRequest.setTb_registration_date(WHPDate.date(DateUtil.now().minusDays(3).toLocalDate()).value());
        return this;
    }

    public PatientRequestBuilder withMandatoryFieldsForPauseTreatment() {
        patientRequest.setCase_id(PATIENT_ID);
        patientRequest.setDate_modified(TODAY_DATE_TIME);
        patientRequest.setTb_id(TB_ID);
        patientRequest.setTreatmentUpdate(TreatmentUpdateScenario.Pause);
        patientRequest.setReason("paws");
        return this;
    }

    public PatientRequestBuilder withMandatoryFieldsForRestartTreatment() {
        patientRequest.setCase_id(PATIENT_ID);
        patientRequest.setDate_modified(TODAY_DATE_TIME);
        patientRequest.setTreatmentUpdate(TreatmentUpdateScenario.Restart);
        patientRequest.setTb_id(TB_ID);
        patientRequest.setReason("swap");
        return this;
    }

    public PatientRequestBuilder withMandatoryFieldsForTransferInTreatment() {
        patientRequest.setProvider_id(NEW_PROVIDER_ID);
        patientRequest.setCase_id(PATIENT_ID);
        patientRequest.setDate_modified(TODAY_DATE_TIME);
        patientRequest.setTb_registration_date(TODAY_DATE);
        patientRequest.setTb_id(NEW_TB_ID);
        patientRequest.setTreatmentUpdate(TreatmentUpdateScenario.New);
        patientRequest.setPatient_type(PatientType.TransferredIn);
        return this;
    }

    public PatientRequestBuilder withMandatoryFieldsForImportPatient() {
        TreatmentCategory category = category01;
        patientRequest = new PatientRequest()
                .setPatientInfo(PATIENT_ID, "Foo", "Bar", Gender.M, PatientType.Chronic, "1234567890", "phi")
                .setTreatmentData(category, TB_ID, "123456", DiseaseClass.P, 50, "registrationNumber", "21/06/2010 10:00:05")
                .addSmearTestResults(SputumTrackingInstance.PreTreatment, DateUtil.newDate(2010, 5, 19), SmearTestResult.Positive, DateUtil.newDate(2010, 5, 21), SmearTestResult.Positive, "labName", "labNumber")
                .setPatientAddress("house number", "landmark", "block", "village", "district", "state");

        patientRequest.setDate_modified("17/03/1990 04:55:00");
        patientRequest.setTb_registration_date("17/03/1990");
        return this;
    }

    public PatientRequestBuilder withProviderId(String providerId) {
        patientRequest.setProvider_id(providerId.toLowerCase());
        return this;
    }

    public PatientRequestBuilder withCaseId(String caseId) {
        patientRequest.setCase_id(caseId.toLowerCase());
        return this;
    }

    public PatientRequestBuilder withTbRegistrationNumber(String tbRegistationNumber) {
        patientRequest.setTb_registration_number(tbRegistationNumber);
        return this;
    }

    public PatientRequestBuilder withPatientInfo(String patientId, String firstName, String lastName, Gender gender, PatientType patientType, String patientMobileNumber, String phi) {
        patientRequest.setPatientInfo(patientId, firstName, lastName, gender, patientType, patientMobileNumber, phi);
        return this;
    }

    public PatientRequestBuilder withFirstName(String firstName) {
        patientRequest.setFirst_name(firstName);
        return this;
    }

    public PatientRequestBuilder withPatientAddress(String houseNumber, String landmark, String block, String village, String district, String state) {
        patientRequest.setPatientAddress(houseNumber, landmark, block, village, district, state);
        return this;
    }

    public PatientRequestBuilder withPatientAge(int age) {
        patientRequest.setAge(age);
        return this;
    }

    public PatientRequestBuilder withSmearTestResults(SputumTrackingInstance smearSputumTrackingInstance, LocalDate smearTestDate1, SmearTestResult smear_result_1, LocalDate smearTestDate2, SmearTestResult smearResult2) {
        patientRequest.addSmearTestResults(smearSputumTrackingInstance, smearTestDate1, smear_result_1, smearTestDate2, smearResult2, "labName", "labNumber");
        return this;
    }

    public PatientRequestBuilder withWeightStatistics(SputumTrackingInstance SputumTrackingInstance, Double weight, LocalDate measuringDate) {
        patientRequest.setWeightStatistics(SputumTrackingInstance, weight, measuringDate);
        return this;
    }

    public PatientRequestBuilder withPatientType(PatientType type) {
        patientRequest.setPatient_type(type);
        return this;
    }

    public PatientRequestBuilder withTbId(String tbId) {
        patientRequest.setTb_id(tbId.toLowerCase());
        return this;
    }

    public PatientRequestBuilder withLastModifiedDate(String lastModifiedDate) {
        patientRequest.setDate_modified(lastModifiedDate);
        return this;
    }

    public PatientRequestBuilder withTreatmentCategory(TreatmentCategory treatmentCategory) {
        patientRequest.setTreatment_category(treatmentCategory);
        return this;
    }

    public PatientRequestBuilder withTreatmentOutcome(TreatmentOutcome treatmentOutcome) {
        patientRequest.setTreatment_outcome(treatmentOutcome);
        return this;
    }

    public PatientRequestBuilder withDateModified(String dateModified) {
        patientRequest.setDate_modified(dateModified);
        return this;
    }

    public PatientRequestBuilder withDiseaseClass(DiseaseClass diseaseClass) {
        patientRequest.setDisease_class(diseaseClass);
        return this;
    }

    public PatientRequestBuilder withTbRegistrationDate(String dateTime) {
        patientRequest.setTb_registration_date(dateTime);
        return this;
    }

    public PatientRequestBuilder withAddressDistrict(String district) {
        patientRequest.setAddress(new Address("house number", "landmark", "block", "village", district, "state"));
        return this;
    }

    public PatientRequestBuilder withCloseTreatmentRemarks(String remarks) {
        patientRequest.setRemarks(remarks);
        return this;
    }

    public PatientRequestBuilder withDefaultTreatmentDetails() {
        patientRequest.setDistrict_with_code("district_with_code");
        patientRequest.setTb_unit_with_code("tb_with_code");
        patientRequest.setEp_site("ep_site");
        patientRequest.setOther_investigations("others");
        patientRequest.setPrevious_treatment_history("treatment_history");
        patientRequest.setHiv_status("hiv_status");
        patientRequest.setHiv_test_date(TODAY_DATE);
        patientRequest.setMembers_below_six_years(6);
        patientRequest.setPhc_referred("phc_referred");
        patientRequest.setProvider_name("provider_name");
        patientRequest.setDot_centre("dot_center");
        patientRequest.setProvider_type("provider_type");
        patientRequest.setCmf_doctor("cmf doctor");
        patientRequest.setContact_person_name("person name");
        patientRequest.setContact_person_phone_number("phone number");
        patientRequest.setXpert_test_result("xpert test result");
        patientRequest.setXpert_device_number("xpert device number");
        patientRequest.setXpert_test_date(TODAY_DATE);
        patientRequest.setRif_resistance_result("rif resistance result");
        return this;
    }

    public PatientRequestBuilder withEmptyTreatmentDetails() {
        patientRequest.setDistrict_with_code("");
        patientRequest.setTb_unit_with_code("");
        patientRequest.setEp_site("");
        patientRequest.setOther_investigations("");
        patientRequest.setPrevious_treatment_history("");
        patientRequest.setHiv_status("");
        patientRequest.setHiv_test_date("");
        patientRequest.setMembers_below_six_years(0);
        patientRequest.setPhc_referred("");
        patientRequest.setProvider_name("");
        patientRequest.setDot_centre("");
        patientRequest.setProvider_type("");
        patientRequest.setCmf_doctor("");
        patientRequest.setContact_person_name("");
        patientRequest.setContact_person_phone_number("");
        patientRequest.setXpert_test_result("");
        patientRequest.setXpert_device_number("");
        patientRequest.setXpert_test_date("");
        patientRequest.setRif_resistance_result("");
        return this;
    }
}
