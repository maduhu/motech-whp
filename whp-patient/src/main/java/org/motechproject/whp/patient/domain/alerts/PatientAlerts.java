package org.motechproject.whp.patient.domain.alerts;

import lombok.Data;
import org.motechproject.whp.common.domain.alerts.PatientAlertType;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.whp.common.domain.alerts.PatientAlertType.AdherenceMissing;
import static org.motechproject.whp.common.domain.alerts.PatientAlertType.CumulativeMissedDoses;
import static org.motechproject.whp.common.domain.alerts.PatientAlertType.TreatmentNotStarted;

@Data
public class PatientAlerts {
    private Map<PatientAlertType, PatientAlert> alerts = new HashMap<>();

    public PatientAlert getAlert(PatientAlertType alertType) {
        if(alerts.containsKey(alertType)){
            return alerts.get(alertType);
        } else {
            PatientAlert patientAlert = new PatientAlert(alertType);
            alerts.put(alertType, patientAlert);
            return patientAlert;
        }
    }

    public PatientAlert cumulativeMissedDoseAlert(){
        return getAlert(CumulativeMissedDoses);
    }

    public PatientAlert adherenceMissingAlert(){
        return getAlert(AdherenceMissing);
    }

    public PatientAlert treatmentNotStartedAlert(){
        return getAlert(TreatmentNotStarted);
    }

    public void updateAlertStatus(PatientAlertType alertType, int value, int severity) {
        getAlert(alertType).update(value, severity);
    }
}