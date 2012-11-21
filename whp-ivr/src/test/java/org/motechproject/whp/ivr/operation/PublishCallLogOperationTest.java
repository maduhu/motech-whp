package org.motechproject.whp.ivr.operation;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.server.kookoo.KookooCallServiceImpl;
import org.motechproject.whp.ivr.CallStatus;
import org.motechproject.whp.ivr.session.IvrSession;
import org.motechproject.whp.ivr.util.FlowSessionStub;
import org.motechproject.whp.ivr.util.SerializableList;
import org.motechproject.whp.reporting.service.ReportingPublisherService;
import org.motechproject.whp.reports.contract.AdherenceCallLogRequest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.now;

public class PublishCallLogOperationTest {

    @Mock
    ReportingPublisherService reportingPublisherService;
    private List<String> patientsWithAdherence;
    private List<String> patientsWithoutAdherence;
    private List<String> patientsWithAdherenceRecordedInThisSession;
    private String callId = "callId";
    private String providerId = "providerId";
    private DateTime startTime = now();
    private DateTime endTime = now().plusMinutes(1);
    private String flashingCallId = "flashingCallId";

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldPublishCallLog() {

        FlowSession flowSession = setUpFlowSession();

        int totalPatients = patientsWithAdherence.size() + patientsWithoutAdherence.size();
        int adherenceCapturedInThisSession = patientsWithAdherenceRecordedInThisSession.size();
        int remainingPatientsWithoutAdherence = patientsWithoutAdherence.size() - patientsWithAdherenceRecordedInThisSession.size();
        CallStatus callStatus = CallStatus.OUTSIDE_ADHERENCE_CAPTURE_WINDOW;

        AdherenceCallLogRequest expectedAdherenceCallLogRequest = createAdherenceCallLogRequest(callStatus, totalPatients, adherenceCapturedInThisSession, remainingPatientsWithoutAdherence);

        PublishCallLogOperation publishCallLogOperation = new PublishCallLogOperation(reportingPublisherService, callStatus, endTime);

        publishCallLogOperation.perform("", flowSession);

        ArgumentCaptor<AdherenceCallLogRequest> argumentCaptor = ArgumentCaptor.forClass(AdherenceCallLogRequest.class);
        verify(reportingPublisherService).reportCallLog(argumentCaptor.capture());

        AdherenceCallLogRequest callLogRequest = argumentCaptor.getValue();

        assertThat(callLogRequest, is(expectedAdherenceCallLogRequest));
    }

    private FlowSession setUpFlowSession() {
        FlowSession flowSession = new FlowSessionStub();
        flowSession.set(IvrSession.PROVIDER_ID, providerId);
        flowSession.set(IvrSession.CALL_START_TIME, startTime);
        flowSession.set(IvrSession.SID, callId);
        flowSession.set(KookooCallServiceImpl.CUSTOM_DATA_KEY, "{\"flashingCallId\": \"" + flashingCallId + "\"}");

        patientsWithAdherence = asList("patient1");
        flowSession.set(IvrSession.PATIENTS_WITH_ADHERENCE, new SerializableList(patientsWithAdherence));
        patientsWithoutAdherence = asList("patient2", "patient3");
        flowSession.set(IvrSession.PATIENTS_WITHOUT_ADHERENCE, new SerializableList(patientsWithoutAdherence));
        patientsWithAdherenceRecordedInThisSession = asList("patient2");
        flowSession.set(IvrSession.PATIENTS_WITH_ADHERENCE_RECORDED_IN_THIS_SESSION, new SerializableList(patientsWithAdherenceRecordedInThisSession));
        return flowSession;
    }

    private AdherenceCallLogRequest createAdherenceCallLogRequest(CallStatus callStatus, int totalPatients, int adherenceCaptured, int remainingPatientsWithoutAdherence) {
        AdherenceCallLogRequest callLogRequest = new AdherenceCallLogRequest();
        callLogRequest.setCallId(callId);
        callLogRequest.setFlashingCallId(flashingCallId);
        callLogRequest.setEndTime(endTime.toDate());
        callLogRequest.setStartTime(startTime.toDate());
        callLogRequest.setCalledBy(providerId);
        callLogRequest.setProviderId(providerId);
        callLogRequest.setTotalPatients(totalPatients);
        callLogRequest.setAdherenceCaptured(adherenceCaptured);
        callLogRequest.setAdherenceNotCaptured(remainingPatientsWithoutAdherence);
        callLogRequest.setCallStatus(callStatus.name());
        return callLogRequest;
    }
}
