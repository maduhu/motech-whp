package org.motechproject.whp.patient.command;

import org.motechproject.whp.common.exception.WHPErrorCode;
import org.motechproject.whp.common.exception.WHPRuntimeException;
import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.Therapy;
import org.motechproject.whp.patient.service.PatientService;
import org.motechproject.whp.patient.service.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransferInPatient extends TreatmentUpdate {

    private TreatmentService treatmentService;

    @Autowired
    public TransferInPatient(PatientService patientService, TreatmentService treatmentService) {
        super(patientService, UpdateScope.transferIn);
        this.treatmentService = treatmentService;
    }

    public void apply(PatientRequest patientRequest) {
        Patient patient = patientService.findByPatientId(patientRequest.getCase_id());
        List<WHPErrorCode> errorCodes = new ArrayList<WHPErrorCode>();
        if (!canTransferInPatient(patient, patientRequest, errorCodes)) {
            throw new WHPRuntimeException(errorCodes);
        }
        treatmentService.transferInPatient(patientRequest);
    }

    private boolean canTransferInPatient(Patient patient, PatientRequest patientRequest, List<WHPErrorCode> errorCodes) {
        if (noCurrentTreatmentExists(patient, errorCodes)) {
            return false;
        } else if (!patient.isCurrentTreatmentClosed()) {
            errorCodes.add(WHPErrorCode.TREATMENT_NOT_CLOSED);
            return false;
        } else if (treatmentDetailsDoNotMatch(patient, patientRequest)) {
            errorCodes.add(WHPErrorCode.TREATMENT_DETAILS_DO_NOT_MATCH);
            return false;
        } else {
            return true;
        }
    }

    private boolean treatmentDetailsDoNotMatch(Patient patient, PatientRequest patientRequest) {
        return notOfSameTreatmentCategory(patient, patientRequest);
    }

    private boolean notOfSameTreatmentCategory(Patient patient, PatientRequest patientRequest) {
        Therapy latestTherapy = patient.getCurrentTherapy();
        return patientRequest.getTreatment_category() != null && !latestTherapy.getTreatmentCategory().equals(patientRequest.getTreatment_category());
    }

}
