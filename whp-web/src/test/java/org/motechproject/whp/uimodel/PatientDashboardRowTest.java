package org.motechproject.whp.uimodel;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.common.domain.Gender;
import org.motechproject.whp.common.domain.Phase;
import org.motechproject.whp.common.domain.alerts.AlertColorConfiguration;
import org.motechproject.whp.common.domain.alerts.PatientAlertType;
import org.motechproject.whp.patient.alerts.processor.CumulativeMissedDosesCalculator;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.builder.TherapyBuilder;
import org.motechproject.whp.patient.builder.TreatmentBuilder;
import org.motechproject.whp.patient.domain.*;
import org.motechproject.whp.patient.domain.alerts.PatientAlerts;
import org.motechproject.whp.user.domain.Provider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.whp.user.builder.ProviderBuilder.newProviderBuilder;

public class PatientDashboardRowTest {

    String firstName = "firstName";
    String lastName = "lastName";
    String patientId = "patientid";
    String phi = "phi";
    Gender gender = Gender.M;
    int patientAge = 20;
    DiseaseClass diseaseClass = DiseaseClass.E;
    String treatmentCategory = "treatmentCategory";
    private String treatmentCategoryCode = "01";
    String patientNumber = "patientNumber";
    String providerMobileNumber = "primaryMobileNumber";
    LocalDate startDate = new LocalDate(2012, 6, 28);
    String tbId = "tbid";
    String providerId = "providerid";
    String tbRegistrationNo = "tbRegistrationNo";
    PatientType patientType = PatientType.New;
    Address patientAddress = new Address("houseNo", "landmark", "block", "village", "district", "state");

    Patient patient;
    Provider provider;
    Treatment currentTreatment;
    TestResults expectedTestResults;
    Therapy therapy;
    PatientAlerts patientAlerts;

    @Mock
    AlertColorConfiguration alertColorConfiguration;
    @Mock
    CumulativeMissedDosesCalculator cumulativeMissedDosesCalculator;

    @Before
    public void setup() {
        initMocks(this);
        currentTreatment = new TreatmentBuilder()
                .withStartDate(new LocalDate(2012, 2, 2))
                .withTbId(tbId)
                .withProviderId(providerId)
                .withProviderDistrict("district")
                .withTbRegistrationNumber(tbRegistrationNo)
                .withPatientType(patientType)
                .withAddress(patientAddress)
                .build();

        treatmentCategoryCode = "01";
        therapy = new TherapyBuilder()
                .withAge(patientAge)
                .withDiseaseClass(diseaseClass)
                .withTherapyUid("therapyUid")
                .withTreatmentCategory(treatmentCategory, treatmentCategoryCode)
                .withStartDate(startDate)
                .withNoOfDosesTaken(Phase.IP, 2)
                .withTreatment(currentTreatment)
                .build();

        provider = newProviderBuilder()
                .withProviderId(providerId)
                .withPrimaryMobileNumber(providerMobileNumber)
                .build();


        patient = new PatientBuilder()
                .withPatientId(patientId)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withPhi(phi)
                .withGender(gender)
                .withPatientMobileNumber(patientNumber)
                .withCurrentTherapy(therapy)
                .withAdherenceMissedWeeks(6, 2, DateUtil.today())
                .withCumulativeMissedAlertValue(10, 2, DateUtil.today())
                .withTreatmentNotStartedDays(8, 2, DateUtil.today())
                .withPatientFlag(true)
                .build();

        patientAlerts = patient.getPatientAlerts();


        expectedTestResults = new TestResults(currentTreatment.getSmearTestResults(), currentTreatment.getWeightStatistics());
    }

    @Test
    public void shouldCreatePatientDashboardRowFromPatient() {

        when(alertColorConfiguration.getColorFor(PatientAlertType.AdherenceMissing, 2)).thenReturn("c2");
        when(alertColorConfiguration.getColorFor(PatientAlertType.CumulativeMissedDoses, 2)).thenReturn("c3");
        when(alertColorConfiguration.getColorFor(PatientAlertType.TreatmentNotStarted, 2)).thenReturn("c4");
        when(cumulativeMissedDosesCalculator.getCumulativeMissedDoses(patient)).thenReturn(10);

        PatientDashboardRow patientDashboardRow = new PatientDashboardRow(patient, alertColorConfiguration, cumulativeMissedDosesCalculator);

        assertThat(patientDashboardRow.getPatientId(), is(patientId));
        assertThat(patientDashboardRow.getFirstName(), is(firstName));
        assertThat(patientDashboardRow.getLastName(), is(lastName));
        assertThat(patientDashboardRow.getAge(), is(patientAge));
        assertThat(patientDashboardRow.getGender(), is(gender.getValue()));
        assertThat(patientDashboardRow.getTbId(), is(tbId));
        assertThat(patientDashboardRow.getCurrentTreatment().getProviderId(), is(providerId));
        assertThat(patientDashboardRow.getCurrentTreatment().getPatientAddress().getAddress_village(), is("village"));
        assertThat(patientDashboardRow.getCurrentTreatment().getProviderDistrict(), is("district"));
        assertThat(patientDashboardRow.getTreatmentCategoryName(), is(treatmentCategory));
        assertThat(patientDashboardRow.getCurrentTreatmentStartDate(), is(currentTreatment.getStartDateAsString()));
        assertThat(patientDashboardRow.getTherapyStartDate(), is(therapy.getStartDateAsString()));
        assertThat(patientDashboardRow.getIpProgress(), is(patient.getIPProgress()));
        assertThat(patientDashboardRow.getCpProgress(), is(patient.getCPProgress()));
        assertThat(patientDashboardRow.isCurrentTreatmentPaused(), is(therapy.isCurrentTreatmentPaused()));

        assertThat(patientDashboardRow.getCumulativeMissedDoses(), is(10));
        assertThat(patientDashboardRow.getTreatmentNotStartedSeverity(), is(2));
        assertThat(patientDashboardRow.getCumulativeMissedDosesSeverity(), is(2));
        assertThat(patientDashboardRow.getAdherenceMissingWeeks(), is(6));
        assertThat(patientDashboardRow.getAdherenceMissingWeeksSeverity(), is(2));
        assertThat(patientDashboardRow.getAdherenceMissingSeverityColor(), is("c2"));
        assertThat(patientDashboardRow.getCumulativeMissedDosesSeverityColor(), is("c3"));
        assertThat(patientDashboardRow.getTreatmentNotStartedSeverityColor(), is("c4"));
        assertThat(patientDashboardRow.getFlag(), is(true));

        verify(cumulativeMissedDosesCalculator).getCumulativeMissedDoses(patient);
    }
}
