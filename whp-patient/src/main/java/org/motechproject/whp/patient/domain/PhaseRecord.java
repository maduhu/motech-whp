package org.motechproject.whp.patient.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;
import org.motechproject.whp.common.domain.Phase;
import org.motechproject.whp.common.util.WHPDate;

import java.io.Serializable;

@Data
public class PhaseRecord implements Serializable {

    private LocalDate startDate;
    private LocalDate endDate;
    private Phase name;
    /*Has to be updated under multiple cases. Identified so far:
    1) CMF Admin adherence update: Triggered by AdherenceController.update() -> PhaseUpdateOrchestrator
    2) Provider adherence update: Triggered by AdherenceController.update() -> PhaseUpdateOrchestrator
    3) CMF Admin startDate/endDate update: Triggered by PatientController.update() -> PhaseUpdateOrchestrator
    4) PhaseRecord transition: TODO
    */

    private PillTakenSummaries pillTakenSummaries = new PillTakenSummaries();

    public PhaseRecord() {
    }

    public PhaseRecord(Phase phaseName) {
        this.name = phaseName;
    }

    public boolean hasStarted() {
        return startDate != null;
    }

    public Integer remainingDoses(TreatmentCategory treatmentCategory) {
        if(treatmentCategory == null)
            return null;

        return treatmentCategory.numberOfDosesForPhase(name) - pillTakenSummaries.getTotalPillsTaken();
    }

    public void start(PhaseRecord previousPhase) {
        this.startDate = previousPhase.endDate.plusDays(1);
    }

    public void stop(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void unset() {
        this.startDate = null;
        this.endDate = null;
    }

    @JsonIgnore
    public String getEndDateAsString() {
        return WHPDate.date(endDate).value();
    }

    @JsonIgnore
    public Integer getNumberOfDosesTaken() {
        return pillTakenSummaries.getTotalPillsTaken();
    }

    @JsonIgnore
    public int getNumberOfDosesTakenAsOfLastSunday(LocalDate referenceDate) {
        return pillTakenSummaries.getTotalPillsTakenTillLastSunday(referenceDate);
    }

    @JsonIgnore
    public void setNumberOfDosesTaken(int dosesTaken, LocalDate asOf) {
        pillTakenSummaries.setPillTakenCount(dosesTaken, asOf);
    }
}

