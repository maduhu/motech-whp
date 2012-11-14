package org.motechproject.whp.ivr.operation;

import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.adherence.audit.contract.AuditParams;
import org.motechproject.whp.adherence.domain.AdherenceSource;
import org.motechproject.whp.adherence.domain.WeeklyAdherenceSummary;
import org.motechproject.whp.applicationservice.orchestrator.TreatmentUpdateOrchestrator;
import org.motechproject.whp.ivr.session.IvrSession;
import org.motechproject.whp.reporting.service.ReportingPublisherService;
import org.motechproject.whp.reports.contract.AdherenceCaptureRequest;

import static org.motechproject.whp.common.domain.TreatmentWeekInstance.currentAdherenceCaptureWeek;
import static org.motechproject.whp.ivr.builder.request.AdherenceCaptureRequestBuilder.adherenceCaptureRequest;

public class RecordAdherenceOperation implements INodeOperation {

    private String currentPatientId;
    private TreatmentUpdateOrchestrator treatmentUpdateOrchestrator;
    private ReportingPublisherService reportingService;

    public RecordAdherenceOperation() {
    }

    public RecordAdherenceOperation(String currentPatientId, TreatmentUpdateOrchestrator treatmentUpdateOrchestrator, ReportingPublisherService reportingService) {
        this.treatmentUpdateOrchestrator = treatmentUpdateOrchestrator;
        this.reportingService = reportingService;
        this.currentPatientId = currentPatientId;
    }

    @Override
    public void perform(String userInput, FlowSession session) {
        IvrSession ivrSession = new IvrSession(session);
        recordAdherence(ivrSession);
        publishAdherenceSubmissionEvent(ivrSession);
        ivrSession.startOfAdherenceSubmission(DateUtil.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordAdherenceOperation that = (RecordAdherenceOperation) o;

        if (currentPatientId != null ? !currentPatientId.equals(that.currentPatientId) : that.currentPatientId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return currentPatientId != null ? currentPatientId.hashCode() : 0;
    }

    private void recordAdherence(IvrSession ivrSession) {
        AuditParams auditParams = new AuditParams(ivrSession.providerId(), AdherenceSource.IVR, "");
        WeeklyAdherenceSummary weeklyAdherenceSummary = new WeeklyAdherenceSummary(currentPatientId, currentAdherenceCaptureWeek());
        weeklyAdherenceSummary.setDosesTaken(Integer.parseInt(ivrSession.adherenceInputForCurrentPatient().input()));
        treatmentUpdateOrchestrator.recordWeeklyAdherence(weeklyAdherenceSummary, currentPatientId, auditParams);
    }

    private void publishAdherenceSubmissionEvent(IvrSession ivrSession) {
        AdherenceCaptureRequest request = adherenceCaptureRequest().validAdherence(currentPatientId, ivrSession);
        reportingService.reportAdherenceCapture(request);
    }

}
