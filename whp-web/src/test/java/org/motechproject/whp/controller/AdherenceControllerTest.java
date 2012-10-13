package org.motechproject.whp.controller;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.adherence.audit.contract.AuditParams;
import org.motechproject.whp.adherence.builder.WeeklyAdherenceSummaryBuilder;
import org.motechproject.whp.adherence.domain.AdherenceSource;
import org.motechproject.whp.adherence.domain.WeeklyAdherenceSummary;
import org.motechproject.whp.adherence.service.WHPAdherenceService;
import org.motechproject.whp.applicationservice.orchestrator.TreatmentUpdateOrchestrator;
import org.motechproject.whp.common.domain.TreatmentWeek;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.service.PatientService;
import org.motechproject.whp.patient.domain.PatientStatus;
import org.motechproject.whp.patient.domain.TreatmentCategory;
import org.motechproject.whp.patient.repository.AllTreatmentCategories;
import org.motechproject.whp.uimodel.WeeklyAdherenceForm;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.model.DayOfWeek.*;
import static org.motechproject.whp.patient.builder.PatientBuilder.PATIENT_ID;

public class AdherenceControllerTest extends BaseControllerTest {

    @Mock
    PatientService patientService;
    @Mock
    AllTreatmentCategories allTreatmentCategories;
    @Mock
    WHPAdherenceService adherenceService;
    @Mock
    Model uiModel;
    @Mock
    HttpServletRequest request;
    @Mock
    private TreatmentUpdateOrchestrator treatmentUpdateOrchestrator;

    private Patient patient;

    private TreatmentCategory category;

    private ArgumentCaptors captors;

    private AdherenceController adherenceController;

    private final String remarks = "remarks";
    private final String providerUserName = "someProviderUserName";
    private final AuditParams auditParams = new AuditParams(providerUserName, AdherenceSource.WEB, remarks);


    @Before
    public void setUp() {
        setUpMocks();
        setUpPatient();
        adherenceController = new AdherenceController(patientService, adherenceService, allTreatmentCategories, treatmentUpdateOrchestrator);
        setupLoggedInUser(request, providerUserName);
    }

    private void setUpMocks() {
        initMocks(this);
        captors = new ArgumentCaptors();
    }

    private void setupTreatmentCategory() {
        category = new TreatmentCategory();
        category.setCode("01");
        category.setDosesPerWeek(3);
        category.setPillDays(asList(Monday, Wednesday, Friday));
        when(allTreatmentCategories.findByCode("01")).thenReturn(category);
    }

    private void setUpPatient() {
        patient = new PatientBuilder().withDefaults().withStatus(PatientStatus.Open).build();
        when(patientService.findByPatientId(patient.getPatientId())).thenReturn(patient);
        setupTreatmentCategory();
    }

    @Test
    public void shouldShowAdherenceCard() {
        WeeklyAdherenceSummary adherenceSummary = new WeeklyAdherenceSummary();
        when(adherenceService.currentWeekAdherence(patient)).thenReturn(adherenceSummary);

        String form = adherenceController.update(PATIENT_ID, uiModel);
        assertEquals("adherence/update", form);
    }

    @Test
    public void shouldPassWeeklyAdherenceLogToAdherenceCard() {
        new WeeklyAdherenceSummaryBuilder().withDosesTaken(3);
        WeeklyAdherenceSummary adherenceSummary = new WeeklyAdherenceSummaryBuilder().build();
        when(adherenceService.currentWeekAdherence(patient)).thenReturn(adherenceSummary);

        adherenceController.update(PATIENT_ID, uiModel);

        verify(uiModel).addAttribute(eq("adherence"), captors.adherenceForm.capture());
        assertEquals(PatientBuilder.PATIENT_ID, captors.adherenceForm.getValue().getPatientId());
    }

    @Test
    public void shouldCaptureAdherence() {
        new WeeklyAdherenceSummaryBuilder().withDosesTaken(3);
        WeeklyAdherenceSummary adherenceSummary = new WeeklyAdherenceSummaryBuilder().build();
        adherenceController.update(PATIENT_ID, remarks, new WeeklyAdherenceForm(adherenceSummary, patient), request);

        ArgumentCaptor<WeeklyAdherenceSummary> captor = forClass(WeeklyAdherenceSummary.class);
        verify(treatmentUpdateOrchestrator).recordWeeklyAdherence(captor.capture(), eq(patient.getPatientId()), eq(auditParams));
        assertEquals(category.getPillDays().size(), captor.getValue().getDosesTaken());
    }

    @Test
    public void shouldShowAnUpdateViewFromSundayTillTuesday() {
        WeeklyAdherenceSummary adherenceSummary = new WeeklyAdherenceSummary();
        when(adherenceService.currentWeekAdherence(patient)).thenReturn(adherenceSummary);

        LocalDate today = DateUtil.today();
        for (LocalDate date :
                asList(
                        today.withDayOfWeek(Sunday.getValue()),
                        today.withDayOfWeek(Monday.getValue()),
                        today.withDayOfWeek(Tuesday.getValue())
                )) {
            mockCurrentDate(date);
            adherenceController.update(PATIENT_ID, uiModel);
            verify(uiModel).addAttribute(eq("readOnly"), eq(false));
            reset(uiModel);
        }
    }

    @Test
    public void shouldShowAReadOnlyViewFromWednesdayTillSaturday() {
        WeeklyAdherenceSummary adherenceSummary = new WeeklyAdherenceSummary();
        when(adherenceService.currentWeekAdherence(patient)).thenReturn(adherenceSummary);

        LocalDate today = DateUtil.today();
        for (LocalDate date :
                asList(
                        today.withDayOfWeek(Wednesday.getValue()),
                        today.withDayOfWeek(Thursday.getValue()),
                        today.withDayOfWeek(Friday.getValue()),
                        today.withDayOfWeek(Saturday.getValue())
                )) {
            mockCurrentDate(date);
            adherenceController.update(PATIENT_ID, uiModel);
            verify(uiModel).addAttribute(eq("readOnly"), eq(true));
            reset(uiModel);
        }
    }

    @Test
    public void shouldShowForwardToProviderHomeAfterCapturingAdherence() {
        new WeeklyAdherenceSummaryBuilder().withDosesTaken(3);
        WeeklyAdherenceSummary adherenceSummary = new WeeklyAdherenceSummaryBuilder().build();
        when(adherenceService.currentWeekAdherence(patient)).thenReturn(adherenceSummary);

        String form = adherenceController.update(PATIENT_ID, remarks, new WeeklyAdherenceForm(adherenceSummary, patient), request);
        assertEquals("redirect:/", form);
    }

    @Test
    public void shouldCaptureAdherenceWithRightValues() {
        WeeklyAdherenceSummary adherence = new WeeklyAdherenceSummary();
        when(adherenceService.currentWeekAdherence(patient)).thenReturn(adherence);

        WeeklyAdherenceForm weeklyAdherenceForm = new WeeklyAdherenceForm(adherence, patient);
        adherenceController.update(PATIENT_ID, remarks, weeklyAdherenceForm, request);

        WeeklyAdherenceSummary weeklyAdherenceSummary = new WeeklyAdherenceSummary(weeklyAdherenceForm.getPatientId(), new TreatmentWeek(weeklyAdherenceForm.getReferenceDate()));
        weeklyAdherenceSummary.setDosesTaken(weeklyAdherenceForm.getNumberOfDosesTaken());

        verify(treatmentUpdateOrchestrator).recordWeeklyAdherence(weeklyAdherenceSummary, patient.getPatientId(), auditParams);
    }


    @After
    public void tearDown() {
        super.tearDown();
    }

    private class ArgumentCaptors {
        private ArgumentCaptor<WeeklyAdherenceForm> adherenceForm = forClass(WeeklyAdherenceForm.class);
    }

}