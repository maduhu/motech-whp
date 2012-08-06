package org.motechproject.whp.ivr.transition;

import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.model.ITransition;
import org.motechproject.decisiontree.model.Node;
import org.motechproject.whp.ivr.WHPIVRMessage;
import org.motechproject.whp.ivr.operation.CaptureAdherenceSubmissionTimeOperation;
import org.motechproject.whp.ivr.session.AdherenceRecordingSession;
import org.motechproject.whp.ivr.session.IvrSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.now;
import static org.motechproject.whp.ivr.prompts.AdherenceSummaryPrompts.adherenceSummaryPrompts;
import static org.motechproject.whp.ivr.prompts.CallCompletionPrompts.adherenceSummaryWithCallCompletionPrompts;
import static org.motechproject.whp.ivr.prompts.CaptureAdherencePrompts.captureAdherencePrompts;


@Component
public class AdherenceSummaryTransition implements ITransition {

    @Autowired
    private WHPIVRMessage whpivrMessage;
    @Autowired
    private AdherenceRecordingSession recordingSession;

    /*Required for platform autowiring*/
    public AdherenceSummaryTransition() {
    }

    public AdherenceSummaryTransition(WHPIVRMessage whpivrMessage, AdherenceRecordingSession recordingSession) {
        this.whpivrMessage = whpivrMessage;
        this.recordingSession = recordingSession;
    }

    @Override
    public Node getDestinationNode(String input, FlowSession flowSession) {
        IvrSession ivrSession = new IvrSession(recordingSession.initialize(flowSession));

        Node captureAdherenceNode = new Node();
        if (ivrSession.hasPatientsWithoutAdherence()) {
            captureAdherenceNode.addPrompts(adherenceSummaryPrompts(whpivrMessage, ivrSession.patientsWithAdherence(), ivrSession.patientsWithoutAdherence()));
            return addAdherenceCaptureTransitions(ivrSession, captureAdherenceNode);
        } else {
            return addTransitionToTerminateCall(captureAdherenceNode, ivrSession.countOfAllPatients(), ivrSession.countOfPatientsWithAdherence());
        }
    }

    private Node addAdherenceCaptureTransitions(final IvrSession ivrSession, Node captureAdherenceNode) {
        captureAdherenceNode.addPrompts(captureAdherencePrompts(whpivrMessage, ivrSession.currentPatientId(), ivrSession.currentPatientNumber()));
        captureAdherenceNode.addOperations(new CaptureAdherenceSubmissionTimeOperation(now()));
        captureAdherenceNode.addTransition("?", new AdherenceCaptureTransition());
        return captureAdherenceNode;
    }

    private Node addTransitionToTerminateCall(Node captureAdherenceNode, Integer countOfAllPatients, Integer countOfPatientsWithAdherence) {
        captureAdherenceNode.addPrompts(adherenceSummaryWithCallCompletionPrompts(whpivrMessage, countOfAllPatients, countOfPatientsWithAdherence));
        return captureAdherenceNode;
    }

}
