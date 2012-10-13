package org.motechproject.whp.adherence.domain;

import lombok.Data;
import org.motechproject.model.DayOfWeek;
import org.motechproject.whp.common.domain.TreatmentWeek;
import org.motechproject.whp.patient.domain.Patient;
import org.motechproject.whp.patient.domain.TreatmentCategory;

import java.util.LinkedList;
import java.util.List;

import static org.motechproject.whp.common.domain.TreatmentWeekInstance.currentAdherenceCaptureWeek;

@Data
public class WeeklyAdherenceSummary {

    private TreatmentWeek week = currentAdherenceCaptureWeek();

    private int dosesTaken;

    private String patientId;

    public WeeklyAdherenceSummary() {
    }

    public WeeklyAdherenceSummary(String patientId, TreatmentWeek week) {
        this.week = week;
        this.patientId = patientId;
    }

    public WeeklyAdherenceSummary(String patientId, TreatmentWeek week, Integer dosesTaken) {
        this.week = week;
        this.patientId = patientId;
        this.dosesTaken = dosesTaken;
    }

    public static WeeklyAdherenceSummary forFirstWeek(Patient patient) {
        TreatmentWeek treatmentWeek = currentAdherenceCaptureWeek();
        return new WeeklyAdherenceSummary(patient.getPatientId(), treatmentWeek);
    }

    public List<DayOfWeek> takenDays(TreatmentCategory treatmentCategory) {
        LinkedList<DayOfWeek> takenDays = new LinkedList<>();
        List<DayOfWeek> daysOfWeek = treatmentCategory.getPillDays();
        for (int i = daysOfWeek.size() - 1; i >= daysOfWeek.size() - dosesTaken; i--) {
            takenDays.push(daysOfWeek.get(i));
        }
        return takenDays;
    }

    public PillStatus pillStatusOn(DayOfWeek pillDay, TreatmentCategory treatmentCategory) {
        if(takenDays(treatmentCategory).contains(pillDay)) return PillStatus.Taken;
        return PillStatus.NotTaken;
    }
}
