package org.motechproject.whp.it.patient.repository.allPatients;

import ch.lambdaj.Lambda;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.paginator.contract.FilterParams;
import org.motechproject.paginator.contract.SortParams;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.common.domain.alerts.PatientAlertType;
import org.motechproject.whp.common.util.WHPDate;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.TreatmentOutcome;
import org.motechproject.whp.patient.query.PatientQueryDefinition;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.joda.time.DateTime.now;

public class PatientFilterTestPart  extends AllPatientsTestPart {

    private Patient patient1;
    private Patient patient2;
    private Patient patient3;
    private Patient patient4WithoutAlerts;
    private Patient inactivePatient;

    @Before
    public void setUp() {
        patient1 = new PatientBuilder().withDefaults()
                .withPatientId("patient1")
                .withProviderId("provider1")
                .withProviderDistrict("district")
                .withCumulativeMissedAlertValue(10, 1)
                .withAdherenceMissedWeeks(3, 1, DateUtil.today().minusDays(7))
                .withTreatmentNotStartedDays(0, 0).build();

        patient2 = new PatientBuilder().withDefaults()
                .withPatientId("patient2")
                .withProviderId("provider1")
                .withProviderDistrict("district")
                .withCumulativeMissedAlertValue(11, 1)
                .withAdherenceMissedWeeks(5, 2, DateUtil.today().minusDays(5))
                .withTreatmentNotStartedDays(0, 0).build();

        patient3 = new PatientBuilder().withDefaults()
                .withPatientId("patient3")
                .withProviderId("provider2")
                .withProviderDistrict("district2")
                .withCumulativeMissedAlertValue(11, 1)
                .withAdherenceMissedWeeks(5, 2, DateUtil.today().minusDays(5))
                .withTreatmentNotStartedDays(0, 0).build();

        patient4WithoutAlerts = new PatientBuilder().withDefaults()
                .withPatientId("patient4")
                .withProviderId("provider3")
                .withProviderDistrict("district3").build();

        inactivePatient = new PatientBuilder().withDefaults()
                .withPatientId("patient5")
                .withProviderId("provider3")
                .withProviderDistrict("district3").build();
        inactivePatient.closeCurrentTreatment(TreatmentOutcome.Cured, now());

        allPatients.add(patient1);
        allPatients.add(patient2);
        allPatients.add(patient3);
        allPatients.add(patient4WithoutAlerts);
        allPatients.add(inactivePatient);
    }

    @Test
    public void shouldReturnAllActivePatients() {
        SortParams sortParams = new SortParams();
        FilterParams queryParams = new FilterParams();

        List<Patient> searchResults =  allPatients.filter(queryParams, sortParams, 0, 5);

        assertEquals(4, searchResults.size());
        assertEquals(4, allPatients.count(queryParams));
        assertTrue(hasNoInactivePatients(searchResults));
    }

    private boolean hasNoInactivePatients(List<Patient> searchResults) {
        return Lambda.filter(having(on(Patient.class).isOnActiveTreatment(), equalTo(false)), searchResults).size() == 0;
    }

    @Test
    public void shouldFilterPatientsByProviderDistrict() {
        SortParams sortParams = new SortParams();
        FilterParams queryParams = new FilterParams();
        queryParams.put("providerDistrict", "district");

        List<Patient> searchResults =  allPatients.filter(queryParams, sortParams, 0, 5);

        assertEquals(2, searchResults.size());
        assertEquals(2, allPatients.count(queryParams));
        assertTrue(hasNoInactivePatients(searchResults));
    }

    @Test
    public void shouldFilterPatientsByProviderId() {
        SortParams sortParams = new SortParams();
        FilterParams queryParams = new FilterParams();
        queryParams.put("providerId", "provider1");

        List<Patient> searchResults =  allPatients.filter(queryParams, sortParams, 0, 5);

        assertEquals(2, searchResults.size());
        assertEquals(2, allPatients.count(queryParams));
        assertTrue(hasNoInactivePatients(searchResults));
    }


    @Test
    public void shouldFilterPatientsByCumulativeMissedDoses() {
        SortParams sortParams = new SortParams();
        FilterParams queryParams = new FilterParams();
        queryParams.put("providerId", "provider1");
        queryParams.put("cumulativeMissedDoses", "10");

        List<Patient> searchResults =  allPatients.filter(queryParams, sortParams, 0, 5);

        assertEquals(1, searchResults.size());
        assertEquals(1, allPatients.count(queryParams));
        assertEquals(patient1.getPatientId(), searchResults.get(0).getPatientId());
        assertTrue(hasNoInactivePatients(searchResults));
    }

    @Test
    public void shouldFilterPatientsByAdherenceMissingWeeks() {
        SortParams sortParams = new SortParams();
        FilterParams queryParams = new FilterParams();
        queryParams.put("providerId", "provider1");
        queryParams.put("adherenceMissingWeeks", "5");

        List<Patient> searchResults =  allPatients.filter(queryParams, sortParams, 0, 5);

        assertEquals(1, searchResults.size());
        assertEquals(1, allPatients.count(queryParams));
        assertEquals(patient2.getPatientId(), searchResults.get(0).getPatientId());
        assertTrue(hasNoInactivePatients(searchResults));
    }

    @Test
    public void shouldFilterPatientsByPatientAlertsSeverity() {
        SortParams sortParams = new SortParams();
        FilterParams queryParams = new FilterParams();
        queryParams.put("providerId", "provider1");
        queryParams.put(PatientAlertType.AdherenceMissing.name() + PatientQueryDefinition.ALERT_SEVERITY, "2");

        List<Patient> searchResults =  allPatients.filter(queryParams, sortParams, 0, 5);

        assertEquals(1, searchResults.size());
        assertEquals(1, allPatients.count(queryParams));
        assertEquals(patient2.getPatientId(), searchResults.get(0).getPatientId());
        assertTrue(hasNoInactivePatients(searchResults));
    }

    @Test
    public void shouldFilterPatientsByPatientAlertsDateRange() {
        SortParams sortParams = new SortParams();
        FilterParams queryParams = new FilterParams();
        queryParams.put("providerId", "provider1");
        String alertDateFrom = WHPDate.date(DateUtil.today().minusDays(5)).value();
        String alertDateTo = WHPDate.date(DateUtil.today().minusDays(5)).value();
        queryParams.put(PatientAlertType.AdherenceMissing.name() + "AlertDateFrom", alertDateFrom);
        queryParams.put(PatientAlertType.AdherenceMissing.name() + "AlertDateTo", alertDateTo);

        List<Patient> searchResults =  allPatients.filter(queryParams, sortParams, 0, 5);

        assertEquals(1, searchResults.size());
        assertEquals(1, allPatients.count(queryParams));
        assertEquals(patient2.getPatientId(), searchResults.get(0).getPatientId());
        assertTrue(hasNoInactivePatients(searchResults));
    }
}
