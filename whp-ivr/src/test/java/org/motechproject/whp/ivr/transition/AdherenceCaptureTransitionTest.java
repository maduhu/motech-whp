package org.motechproject.whp.ivr.transition;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.decisiontree.model.Prompt;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.ivr.CallStatus;
import org.motechproject.whp.ivr.WhpIvrMessage;
import org.motechproject.whp.ivr.builder.PromptBuilder;
import org.motechproject.whp.ivr.operation.InvalidAdherenceOperation;
import org.motechproject.whp.ivr.operation.NoInputAdherenceOperation;
import org.motechproject.whp.ivr.operation.PublishCallLogOperation;
import org.motechproject.whp.ivr.operation.SkipAdherenceOperation;
import org.motechproject.whp.ivr.prompts.CaptureAdherencePrompts;
import org.motechproject.whp.ivr.prompts.InvalidAdherencePrompts;
import org.motechproject.whp.ivr.session.IvrSession;
import org.motechproject.whp.ivr.util.FlowSessionStub;
import org.motechproject.whp.ivr.util.SerializableList;
import org.motechproject.whp.patient.builder.PatientBuilder;
import org.motechproject.whp.patient.builder.TreatmentBuilder;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.Therapy;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.service.PatientService;
import org.motechproject.whp.refdata.domain.DiseaseClass;
import org.motechproject.whp.refdata.domain.Gender;
import org.motechproject.whp.refdata.domain.TreatmentCategory;
import org.motechproject.whp.reporting.service.ReportingPublisherService;

import java.util.Properties;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.whp.ivr.IvrAudioFiles.*;
import static org.motechproject.whp.ivr.prompts.CallCompletionPrompts.callCompletionPromptsAfterCapturingAdherence;
import static org.motechproject.whp.ivr.prompts.CaptureAdherencePrompts.captureAdherencePrompts;
import static org.motechproject.whp.ivr.session.IvrSession.CURRENT_NO_INPUT_RETRY_COUNT;
import static org.motechproject.whp.ivr.session.IvrSession.CURRENT_PATIENT_INDEX;
import static org.motechproject.whp.ivr.session.IvrSession.PROVIDER_ID;
import static org.motechproject.whp.ivr.transition.TransitionToCollectPatientAdherence.INVALID_INPUT_THRESHOLD_KEY;
import static org.motechproject.whp.ivr.transition.TransitionToCollectPatientAdherence.NO_INPUT_THRESHOLD_KEY;
import static org.motechproject.whp.ivr.util.PlatformStub.play;
import static org.motechproject.whp.ivr.util.PlatformStub.replay;

public class AdherenceCaptureTransitionTest extends BaseUnitTest {

    @Mock
    PatientService patientService;
    @Mock
    private ReportingPublisherService reportingPublisherService;

    @Mock
    private Properties ivrProperties;

    FlowSession flowSession;
    WhpIvrMessage whpIvrMessage;
    AdherenceCaptureTransition adherenceCaptureTransition;

    String providerId = "providerid";
    String patientId1 = "patientid";
    String patientId2 = "someOtherPatientId";

    @Before
    public void setUp() {
        initMocks(this);
        whpIvrMessage = new WhpIvrMessage(new Properties());
        flowSession = new FlowSessionStub();
        IvrSession ivrSession = new IvrSession(flowSession);
        flowSession.set(IvrSession.PATIENTS_WITHOUT_ADHERENCE, new SerializableList(asList(patientId1, patientId2)));
        ivrSession.providerId(PROVIDER_ID);
        ivrSession.callId("callId");

        Patient patient = getPatientFor3DosesPerWeek(patientId1);

        adherenceCaptureTransition = new AdherenceCaptureTransition(whpIvrMessage, patientService, reportingPublisherService,ivrProperties);

        when(patientService.findByPatientId(patientId1)).thenReturn(patient);
        when(ivrProperties.getProperty(NO_INPUT_THRESHOLD_KEY)).thenReturn("2");
        when(ivrProperties.getProperty(INVALID_INPUT_THRESHOLD_KEY)).thenReturn("2");
    }

    @Test
    public void shouldSkipCurrentPatientIfKey9IsPressed() {
        PromptBuilder promptBuilder = new PromptBuilder(whpIvrMessage).wav(PATIENT_LIST)
                .number(2)
                .id(patientId2)
                .wav(ENTER_ADHERENCE);
        Node expectedNode = new Node().addPrompts(promptBuilder.build())
                .addTransition("?", new AdherenceCaptureTransition());

        Node destinationNode = adherenceCaptureTransition.getDestinationNode("9", flowSession);
        assertEquals(expectedNode, destinationNode);
    }

    @Test
    public void shouldPlayEndOfCallPromptsIfAdherenceIsSkippedForTheLastPatient() {
        new IvrSession(flowSession).currentPatientIndex(1);
        PromptBuilder promptBuilder = new PromptBuilder(whpIvrMessage)
                .wav(THANK_YOU)
                .wav(END_OF_CALL_ADHERENCE_PROVIDED_FOR)
                .number(2)
                .wav(END_OF_CALL_ADHERENCE_OUT_OF)
                .number(0)
                .wav(END_OF_CALL_ADHERENCE_TOTAL_PATIENTS)
                .wav(CALL_BACK_MESSAGE)
                .wav(COMPLETION_MESSAGE)
                .wav(MUSIC_END_NOTE);

        Node expectedNode = new Node().addPrompts(promptBuilder.build());

        Node destinationNode = adherenceCaptureTransition.getDestinationNode("9", flowSession);
        assertEquals(expectedNode, destinationNode);
    }

    @Test
    public void shouldRecordAdherenceForValidInput() {
        int dosesTaken = 2;
        int dosesPerWeek = 3;
        PromptBuilder promptBuilder = new PromptBuilder(whpIvrMessage)
                .wav(PATIENT)
                .id(patientId1)
                .wav(HAS_TAKEN)
                .number(dosesPerWeek)
                .wav(OUT_OF)
                .number(dosesTaken)
                .wav(DOSES)
                .wav(CONFIRM_ADHERENCE);

        Node expectedNode = new Node().addPrompts(promptBuilder.build())
                .addTransition("?", new ConfirmAdherenceTransition());

        Node destinationNode = adherenceCaptureTransition.getDestinationNode(String.valueOf(dosesTaken), flowSession);
        assertEquals(expectedNode, destinationNode);
    }

    @Test
    public void shouldHangUpWithCallCompletionSummaryIfPatientListIsEmpty() {
        Patient patient = new PatientBuilder().withPatientId("patient1").withAdherenceProvidedForLastWeek().build();

        flowSession.set(IvrSession.PATIENTS_WITHOUT_ADHERENCE, new SerializableList(asList(patientId1)));
        flowSession.set(IvrSession.PATIENTS_WITH_ADHERENCE, new SerializableList(asList(patient.getPatientId())));

        Node expectedNode = new Node()
                .addPrompts(callCompletionPromptsAfterCapturingAdherence(whpIvrMessage, 2, 1));

        Node destinationNode = adherenceCaptureTransition.getDestinationNode("9", flowSession);
        assertEquals(expectedNode, destinationNode);
    }

    @Test
    public void shouldAddPublishCallLogOperationAtEndOfCall() {
        Patient patient = new PatientBuilder().withPatientId("patient1").withAdherenceProvidedForLastWeek().build();

        flowSession.set(IvrSession.PATIENTS_WITHOUT_ADHERENCE, new SerializableList(asList(patientId1)));
        flowSession.set(IvrSession.PATIENTS_WITH_ADHERENCE, new SerializableList(asList(patient.getPatientId())));

        mockCurrentDate(DateUtil.now());

        DateTime now = DateUtil.now();
        PublishCallLogOperation publishCallLogOperation = new PublishCallLogOperation(reportingPublisherService, CallStatus.VALID_ADHERENCE_CAPTURE, now);

        Node destinationNode = adherenceCaptureTransition.getDestinationNode("9", flowSession);
        assertThat(destinationNode.getOperations(), hasItem(publishCallLogOperation));
    }

    @Test
    public void shouldBuildNodeThatResetsAdherenceSubmissionTimeIfPatientDoseIsSkipped() {
        DateTime now = new DateTime(2011, 1, 1, 1, 1, 1, 1);
        mockCurrentDate(now);

        new IvrSession(flowSession).startOfAdherenceSubmission(now.minusDays(5));
        play(adherenceCaptureTransition, flowSession, "9");
        Assert.assertEquals(now, new IvrSession(flowSession).startOfAdherenceSubmission());
    }

    @Test
    public void shouldReportWhenOnSkipAdherence() {
        Node destinationNode = adherenceCaptureTransition.getDestinationNode("9", flowSession);
        assertThat(destinationNode.getOperations(), hasItem(isA(SkipAdherenceOperation.class)));
    }

    @Test
    public void shouldBuildNodeThatDoesNotResetAdherenceSubmissionTimeIfPatientDoseIsInvalid() {
        DateTime now = new DateTime(2011, 1, 1, 1, 1, 1, 1);
        mockCurrentDate(now);

        DateTime oldTime = now.minusDays(5);
        new IvrSession(flowSession).startOfAdherenceSubmission(oldTime);
        play(adherenceCaptureTransition, flowSession, "8");
        Assert.assertEquals(oldTime, new IvrSession(flowSession).startOfAdherenceSubmission());
    }

    @Test
    public void shouldBuildNodeThatDoesNotResetAdherenceSubmissionTimeIfPatientDoseIsValid() {
        DateTime now = new DateTime(2011, 1, 1, 1, 1, 1, 1);
        mockCurrentDate(now);

        DateTime oldTime = now.minusDays(5);
        new IvrSession(flowSession).startOfAdherenceSubmission(oldTime);
        play(adherenceCaptureTransition, flowSession, "1");
        Assert.assertEquals(oldTime, new IvrSession(flowSession).startOfAdherenceSubmission());
    }

    @Test
    public void shouldNotResetAdherenceSubmissionTimeWhenReplayed() {
        DateTime now = new DateTime(2011, 1, 1, 1, 1, 1, 1);
        mockCurrentDate(now);

        DateTime oldTime = now.minusDays(5);
        new IvrSession(flowSession).startOfAdherenceSubmission(oldTime);
        replay(adherenceCaptureTransition, flowSession, "1");
        Assert.assertEquals(oldTime, new IvrSession(flowSession).startOfAdherenceSubmission());
    }

    @Test
    public void shouldRepeatOnNoInput_withinRetryThreshold() {
        Prompt[] expectedPrompts = CaptureAdherencePrompts.captureAdherencePrompts(whpIvrMessage, patientId1, 1);
        flowSession.set(CURRENT_NO_INPUT_RETRY_COUNT, 1);

        Node destinationNode = adherenceCaptureTransition.getDestinationNode("", flowSession);

        assertThat(destinationNode.getPrompts(), hasItems(expectedPrompts));
        assertThat(destinationNode.getPrompts().size(), is(expectedPrompts.length));
        assertTrue(destinationNode.getTransitions().get("?") instanceof AdherenceCaptureTransition);
    }

    @Test
    public void shouldReportOnNoInput_withinRetryThreshold() {
        flowSession.set(CURRENT_NO_INPUT_RETRY_COUNT, 1);
        Node destinationNode = adherenceCaptureTransition.getDestinationNode("", flowSession);
        assertThat(destinationNode.getOperations(), hasItem(isA(NoInputAdherenceOperation.class)));
    }

    @Test
    public void shouldAddInvalidAdherenceOperationForInvalidInput_withinRetryThreshold() {
        flowSession.set(CURRENT_NO_INPUT_RETRY_COUNT, 1);
        Node node = adherenceCaptureTransition.getDestinationNode("8", flowSession);
        assertThat(node.getOperations(), hasItem(isA(InvalidAdherenceOperation.class)));
    }

    @Test
    public void shouldAddInvalidAdherencePromptsOnInvalidInput_withinRetryThreshold() {
        flowSession.set(CURRENT_NO_INPUT_RETRY_COUNT, 1);
        Node node = adherenceCaptureTransition.getDestinationNode("8", flowSession);

        assertThat(node.getTransitions().size(), is(1));
        assertThat(node.getTransitions().get("?"), instanceOf(AdherenceCaptureTransition.class));

        Patient patient1 = new PatientBuilder().withDefaults().withPatientId("patient1").build();
        Prompt[] expectedPrompts = InvalidAdherencePrompts.invalidAdherencePrompts(whpIvrMessage, patient1.getCurrentTherapy().getTreatmentCategory());
        assertThat(node.getPrompts(), hasItems(expectedPrompts));
    }

    @Test
    public void shouldAddCaptureAdherencePromptsAndTransitionForCurrentPatientOnInvalidInput_withinRetryThreshold() {
        flowSession.set(CURRENT_NO_INPUT_RETRY_COUNT, 1);
        flowSession.set(CURRENT_PATIENT_INDEX, 0);
        Prompt[] expectedPrompts = captureAdherencePrompts(whpIvrMessage, new IvrSession(flowSession));

        Node node = adherenceCaptureTransition.getDestinationNode("4", flowSession);

        assertThat(node.getPrompts(), hasItems(expectedPrompts));
        assertThat(node.getTransitions().size(), is(1));
        assertThat((AdherenceCaptureTransition) node.getTransitions().get("?"), is(new AdherenceCaptureTransition()));
        assertThat((Integer)flowSession.get(CURRENT_PATIENT_INDEX), is(0));
    }

    private Patient getPatientFor3DosesPerWeek(String patientId) {
        TreatmentCategory treatmentCategory = new TreatmentCategory();
        treatmentCategory.setDosesPerWeek(3);
        Therapy therapy = new Therapy(treatmentCategory, DiseaseClass.P, 20);
        Patient patient = new Patient(patientId, "", "", Gender.F, "");
        Treatment treatment = new TreatmentBuilder().withProviderId(providerId).build();
        patient.addTreatment(treatment, therapy, DateUtil.now());
        return patient;
    }

}
