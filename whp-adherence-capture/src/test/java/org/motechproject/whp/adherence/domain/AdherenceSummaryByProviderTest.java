package org.motechproject.whp.adherence.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.domain.Patient;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

public class AdherenceSummaryByProviderTest {

    public static final String PROVIDER_ID = "providerId";

    public List<Patient> patientsWithAdherence;
    public List<Patient> patientsWithoutAdherence;
    public List<Patient> patients;

    @Before
    public void setUp() {
        Patient patientWithAdherence1 = new PatientBuilder().withDefaults().withPatientId("patient1").withTherapyStartDate(new LocalDate(2012,7,7)).withAdherenceProvidedForLastWeek().build();
        Patient patientWithAdherence2 = new PatientBuilder().withDefaults().withPatientId("patient2").withTherapyStartDate(new LocalDate(2012,7,7)).withAdherenceProvidedForLastWeek().build();

        patientsWithAdherence = asList(patientWithAdherence1, patientWithAdherence2);

        Patient patientWithoutAdherence1 = new PatientBuilder().withDefaults().withPatientId("patient3").build();
        Patient patientWithoutAdherence2 = new PatientBuilder().withDefaults().withPatientId("patient4").build();
        patientsWithoutAdherence = asList(patientWithoutAdherence1, patientWithoutAdherence2);

        patients = new ArrayList<>();
        patients.addAll(patientsWithAdherence);
        patients.addAll(patientsWithoutAdherence);
    }
    @Test
    public void shouldReturnZeroAsCountOfAllPatientsByDefault() {
        AdherenceSummaryByProvider summary = new AdherenceSummaryByProvider(PROVIDER_ID, new ArrayList<Patient>());
        assertThat(summary.countOfAllPatients(), is(0));
    }

    @Test
    public void shouldCountAllPatientsUnderProvider() {
        AdherenceSummaryByProvider summary = new AdherenceSummaryByProvider(PROVIDER_ID, patients);
        assertThat(summary.countOfAllPatients(), is(patients.size()));
    }

    @Test
    public void shouldReturnZeroAsCountAllPatientsWithAdherenceByDefault() {
        AdherenceSummaryByProvider summary = new AdherenceSummaryByProvider(PROVIDER_ID, new ArrayList<Patient>());
        assertThat(summary.countOfPatientsWithAdherence(), is(0));
    }

    @Test
    public void shouldCountAllPatientsWithAdherence() {
        AdherenceSummaryByProvider summary = new AdherenceSummaryByProvider(PROVIDER_ID, patients);
        assertThat(summary.countOfPatientsWithAdherence(), is(patientsWithAdherence.size()));
    }

    @Test
    public void shouldReturnZeroAsCountOfAllPatientsWithoutAdherenceByDefault() {
        AdherenceSummaryByProvider summary = new AdherenceSummaryByProvider(PROVIDER_ID, new ArrayList<Patient>());
        assertThat(summary.countOfPatientsWithoutAdherence(), is(0));
    }

    @Test
    public void shouldReturnCountOfPatientsWithoutAdherence() {
        AdherenceSummaryByProvider summary = new AdherenceSummaryByProvider(PROVIDER_ID, patients);
        assertThat(summary.countOfPatientsWithoutAdherence(), is(patientsWithoutAdherence.size()));
    }

    @Test
    public void shouldReturnAllPatientsWithoutAdherence() {
        AdherenceSummaryByProvider adherenceSummaryByProvider = new AdherenceSummaryByProvider("providerId",patients);
        assertThat(adherenceSummaryByProvider.getAllPatientsWithoutAdherence(), is(patientsWithoutAdherence));
    }

    @Test
    public void shouldReturnAllPatientsWithAdherence() {
        AdherenceSummaryByProvider adherenceSummaryByProvider = new AdherenceSummaryByProvider("providerId",patients);
        assertThat(adherenceSummaryByProvider.getAllPatientsWithAdherence(), is(patientsWithAdherence));
    }

    @Test
    public void shouldReturnAllPatientsWithAdherenceForCurrentTherapyOnly() {
        Patient patientWithAdherenceForPreviousTherapy = new PatientBuilder().withPatientId("patient1").withAdherenceProvidedForLastWeek().build();
        patients.add(patientWithAdherenceForPreviousTherapy);
        AdherenceSummaryByProvider adherenceSummaryByProvider = new AdherenceSummaryByProvider("providerId", patients);
        assertThat(adherenceSummaryByProvider.getAllPatientsWithAdherence(), hasItem(not(patientWithAdherenceForPreviousTherapy)));
    }

    @Test
    public void shouldReturnListOfPatientIdsWithoutAdherence() {
        AdherenceSummaryByProvider adherenceSummaryByProvider = new AdherenceSummaryByProvider("providerId", patients);

        assertEquals(asList("patient3", "patient4"), adherenceSummaryByProvider.getAllPatientIdsWithoutAdherence());
    }

}
