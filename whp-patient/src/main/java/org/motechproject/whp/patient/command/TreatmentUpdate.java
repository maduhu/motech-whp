package org.motechproject.whp.patient.command;

import org.motechproject.whp.common.exception.WHPErrorCode;
import org.motechproject.whp.patient.domain.Treatment;
import org.motechproject.whp.patient.service.PatientService;

import java.util.List;

public abstract class TreatmentUpdate extends UpdateCommand {

    protected TreatmentUpdate(PatientService patientService, UpdateScope command) {
        super(patientService, command);
    }

    protected boolean updatingCurrentTreatment(String tbId, Treatment currentTreatment, List<WHPErrorCode> errorCodes) {
        boolean tbIdMatches = true;
        if (!currentTreatment.getTbId().equalsIgnoreCase(tbId)) {
            errorCodes.add(WHPErrorCode.TB_ID_DOES_NOT_MATCH);
            tbIdMatches = false;
        }
        return tbIdMatches;
    }
}
