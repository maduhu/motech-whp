package org.motechproject.whp.patient.domain.criteria;

import org.motechproject.whp.patient.contract.PatientRequest;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.ProvidedTreatment;
import org.motechproject.whp.patient.domain.Therapy;
import org.motechproject.whp.patient.domain.Therapy;
import org.motechproject.whp.patient.exception.WHPErrorCode;

import java.util.List;

public class UpdatePatientCriteria {

    public static boolean canOpenNewTreatment(Patient patient, List<WHPErrorCode> errorCodes) {
        if (sanityCheckFails(patient, errorCodes)) return false;
        if (!patient.isCurrentTreatmentClosed()) {
            errorCodes.add(WHPErrorCode.TREATMENT_NOT_CLOSED);
            return false;
        }
        return true;
    }

    public static boolean canCloseCurrentTreatment(Patient patient, PatientRequest patientRequest, List<WHPErrorCode> errorCodes) {
        if (sanityCheckFails(patient, errorCodes)) return false;
        ProvidedTreatment currentProvidedTreatment = patient.getCurrentProvidedTreatment();
        boolean tbIdMatches = true;
        tbIdMatches = VerifyAndAddErrorCodeForTbIdValidation(patientRequest, errorCodes, currentProvidedTreatment);
        boolean endDateNotSet = true;
        if (patient.isCurrentTreatmentClosed()) {
            errorCodes.add(WHPErrorCode.TREATMENT_ALREADY_CLOSED);
            endDateNotSet = false;
        }
        return tbIdMatches && endDateNotSet;
    }

    public static boolean canPerformSimpleUpdate(Patient patient, PatientRequest patientRequest, List<WHPErrorCode> errorCodes) {
        if (sanityCheckFails(patient, errorCodes)) return false;
        ProvidedTreatment currentProvidedTreatment = patient.getCurrentProvidedTreatment();
        if (currentProvidedTreatment.getTbId().compareToIgnoreCase(patientRequest.getTb_id())!=0) {
            errorCodes.add(WHPErrorCode.TB_ID_DOES_NOT_MATCH);
            return false;
        }
        return true;
    }

    public static boolean canTransferInPatient(Patient patient, PatientRequest patientRequest, List<WHPErrorCode> errorCodes) {
        if (sanityCheckFails(patient, errorCodes)) return false;
        if (!canOpenNewTreatment(patient, errorCodes)) {
            errorCodes.add(WHPErrorCode.TREATMENT_NOT_CLOSED);
            return false;
        }
        Therapy latestTherapy = patient.latestTreatment();

        if (!latestTherapy.getDiseaseClass().equals(patientRequest.getDisease_class())
                || !latestTherapy.getTreatmentCategory().equals(patientRequest.getTreatment_category())) {
            errorCodes.add(WHPErrorCode.TREATMENT_DETAILS_DO_NOT_MATCH);
            return false;
        }
        return true;
    }

    public static boolean canPauseCurrentTreatment(Patient patient, PatientRequest patientRequest, List<WHPErrorCode> errorCodes) {
        if (sanityCheckFails(patient, errorCodes)) return false;
        ProvidedTreatment currentProvidedTreatment = patient.getCurrentProvidedTreatment();
        boolean tbIdMatches = true;
        tbIdMatches = VerifyAndAddErrorCodeForTbIdValidation(patientRequest, errorCodes, currentProvidedTreatment);
        if (patient.isCurrentTreatmentClosed()) {
            errorCodes.add(WHPErrorCode.TREATMENT_ALREADY_CLOSED);
            return false;
        }
        if (currentProvidedTreatment.isPaused()) {
            errorCodes.add(WHPErrorCode.TREATMENT_ALREADY_PAUSED);
            return false;
        }
        return tbIdMatches;
    }

    private static boolean VerifyAndAddErrorCodeForTbIdValidation(PatientRequest patientRequest, List<WHPErrorCode> errorCodes, ProvidedTreatment currentProvidedTreatment) {
        boolean  tbIdMatches = true;
        if (currentProvidedTreatment.getTbId().compareToIgnoreCase(patientRequest.getTb_id())!=0) {
            errorCodes.add(WHPErrorCode.TB_ID_DOES_NOT_MATCH);
            tbIdMatches = false;
        }
        return tbIdMatches;
    }

    public static boolean canRestartCurrentTreatment(Patient patient, PatientRequest patientRequest, List<WHPErrorCode> errorCodes) {
        if (sanityCheckFails(patient, errorCodes)) return false;
        ProvidedTreatment currentProvidedTreatment = patient.getCurrentProvidedTreatment();
        boolean tbIdMatches = true;
        tbIdMatches = VerifyAndAddErrorCodeForTbIdValidation(patientRequest, errorCodes, currentProvidedTreatment);
        boolean treatmentIsPaused = true;
        if (!currentProvidedTreatment.isPaused()) {
            errorCodes.add(WHPErrorCode.TREATMENT_ALREADY_IN_PROGRESS);
            treatmentIsPaused = false;
        }
        return tbIdMatches && treatmentIsPaused;
    }

    public static boolean sanityCheckFails(Patient patient, List<WHPErrorCode> errorCodes) {
        if (patient == null) {
            errorCodes.add(WHPErrorCode.CASE_ID_DOES_NOT_EXIST);
            return true;
        }
        if (!patient.hasCurrentTreatment()) {
            errorCodes.add(WHPErrorCode.NO_EXISTING_TREATMENT_FOR_CASE);
            return true;
        }
        return false;
    }
}


