package org.motechproject.whp.it.adherence.capture.service;

import junit.framework.Assert;
import org.ektorp.CouchDbConnector;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.adherence.audit.contract.AuditParams;
import org.motechproject.whp.adherence.audit.repository.AllDailyAdherenceAuditLogs;
import org.motechproject.whp.adherence.audit.repository.AllWeeklyAdherenceAuditLogs;
import org.motechproject.whp.adherence.builder.WeeklyAdherenceSummaryBuilder;
import org.motechproject.whp.adherence.criteria.TherapyStartCriteria;
import org.motechproject.whp.adherence.domain.*;
import org.motechproject.whp.adherence.mapping.AdherenceListMapper;
import org.motechproject.whp.adherence.repository.AllAdherenceLogs;
import org.motechproject.whp.adherence.service.WHPAdherenceService;
import org.motechproject.whp.common.domain.District;
import org.motechproject.whp.common.repository.AllDistricts;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.builder.PatientRequestBuilder;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.PatientType;
import org.motechproject.whp.patient.repository.AllPatients;
import org.motechproject.whp.patient.service.PatientService;
import org.motechproject.whp.patient.service.TreatmentService;
import org.motechproject.whp.user.builder.ProviderBuilder;
import org.motechproject.whp.user.domain.Provider;
import org.motechproject.whp.user.repository.AllProviders;
import org.motechproject.whp.webservice.service.PatientWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = "classpath*:/applicationITContext.xml")
public abstract class WHPAdherenceServiceTestPart extends SpringIntegrationTest {

    LocalDate today = DateUtil.newDate(2012, 5, 3);

    @Autowired
    @Qualifier(value = "whpDbConnector")
    CouchDbConnector couchDbConnector;

    @Autowired
    @Qualifier(value = "adherenceDbConnector")
    CouchDbConnector adherenceDbConnector;
    @Autowired
    @Qualifier(value = "whpDbConnector")
    CouchDbConnector dailyAdherenceDbConnector;
    @Autowired
    WHPAdherenceService adherenceService;

    @Autowired
    TreatmentService treatmentService;

    @Autowired
    PatientService patientService;
    @Autowired
    PatientWebService patientWebService;
    @Autowired
    AllAdherenceLogs allAdherenceLogs;
    @Autowired
    AllPatients allPatients;
    @Autowired
    AllWeeklyAdherenceAuditLogs allWeeklyAdherenceAuditLogs;

    @Autowired
    AllProviders allProviders;

    @Autowired
    AllDailyAdherenceAuditLogs allDailyAdherenceAuditLogs;
    @Autowired
    private AllDistricts allDistricts;
    final AuditParams auditParams = new AuditParams("user", AdherenceSource.WEB, "remarks");
    final String THERAPY_DOC_ID = "THERAPY_DOC_ID";
    private Provider provider;
    private Provider newProvider;
    private District district;

    @Before
    public void setup() {
        allWeeklyAdherenceAuditLogs.removeAll();

        provider = new ProviderBuilder().withProviderId(PatientBuilder.PROVIDER_ID).withDistrict("district").build();
        newProvider = new ProviderBuilder().withProviderId(PatientRequestBuilder.NEW_PROVIDER_ID).withDistrict("district").build();

        mockCurrentDate(today);
        allProviders.add(provider);
        allProviders.add(newProvider);
        district = new District("district");
        allDistricts.add(district);
    }

    @After
    public void tearDown() {
        super.tearDown();
        deleteAdherenceLogs();
        allPatients.removeAll();
        allWeeklyAdherenceAuditLogs.removeAll();
        allProviders.remove(provider);
        allProviders.remove(newProvider);
        allDistricts.remove(district);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }

    private void deleteAdherenceLogs() {
        for (Object log : allAdherenceLogs.getAll().toArray()) {
            adherenceDbConnector.delete(log);
        }

        for (Object log : allDailyAdherenceAuditLogs.getAll().toArray())
            dailyAdherenceDbConnector.delete(log);
    }

    protected Adherence createLog(String patientId, LocalDate pillDate, PillStatus pillStatus, String tbId, String therapyUid, String providerId) {
        Adherence log = new Adherence();
        log.setTbId(tbId);
        log.setProviderId(providerId);
        log.setPillStatus(pillStatus);
        log.setTreatmentId(therapyUid);
        log.setPillDate(pillDate);
        log.setPatientId(patientId);
        return log;
    }

    protected Patient createPatient(PatientRequest request) {
        patientService.createPatient(request);
        return allPatients.findByPatientId(request.getCase_id());
    }

    protected void startTreatment(PatientRequest request) {
        treatmentService.openTreatment(request);
    }


    protected WeeklyAdherenceSummary recordAdherence() {
        Patient patient = allPatients.findByPatientId(PatientBuilder.PATIENT_ID);
        WeeklyAdherenceSummary weeklyAdherenceSummary = new WeeklyAdherenceSummaryBuilder().withDosesTaken(1).forPatient(patient).build();
        AdherenceList adherenceList = AdherenceListMapper.map(patient, weeklyAdherenceSummary);
        if (TherapyStartCriteria.shouldStartOrRestartTreatment(patient, weeklyAdherenceSummary)) {
            patient.startTherapy(adherenceList.firstDoseTakenOn());
        }
        adherenceService.recordWeeklyAdherence(adherenceList, weeklyAdherenceSummary, patient, auditParams);
        return weeklyAdherenceSummary;
    }

    protected void adherenceIsRecordedForTheFirstTime() {
        WeeklyAdherenceSummary weeklyAdherenceSummary = new WeeklyAdherenceSummaryBuilder().withDosesTaken(3).build();
        PatientRequest patientRequest = new PatientRequestBuilder().withDefaults()
                .withPatientType(PatientType.New)
                .withLastModifiedDate("17/03/1990 04:55:50")
                .build();
        Patient patient = createPatient(patientRequest);
        AdherenceList adherenceList = AdherenceListMapper.map(patient, weeklyAdherenceSummary);
        if (TherapyStartCriteria.shouldStartOrRestartTreatment(patient, weeklyAdherenceSummary)) {
            patient.startTherapy(adherenceList.firstDoseTakenOn());
            allPatients.update(patient);
        }
        adherenceService.recordWeeklyAdherence(adherenceList, weeklyAdherenceSummary, patient, auditParams);
    }

    protected void assertTbAndProviderId(Adherence adherence, String expectedTbId, String expectedProviderId) {
        Assert.assertEquals(expectedTbId, adherence.getTbId());
        Assert.assertEquals(expectedProviderId, adherence.getProviderId());
    }
}
