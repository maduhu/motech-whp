package org.motechproject.whp.treatmentcard.domain;

import lombok.Getter;
import org.joda.time.LocalDate;
import org.motechproject.model.DayOfWeek;
import org.motechproject.whp.adherence.domain.Adherence;
import org.motechproject.whp.patient.domain.*;
import org.motechproject.whp.common.domain.Phase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.motechproject.util.DateUtil.today;

@Getter
public class TreatmentCard {

    public static final int MONTHS_IN_IP_BOX = 5;
    public static final int MONTHS_IN_CP_BOX = 7;

    private Patient patient;
    private boolean isSundayDoseDate;
    private Therapy therapy;
    private List<TreatmentHistory> treatmentHistories = new ArrayList();
    private List<TreatmentPausePeriod> treatmentPausePeriods = new ArrayList();
    private AdherenceSection ipAndEipAdherenceSection = new AdherenceSection();
    private AdherenceSection cpAdherenceSection = new AdherenceSection();

    public TreatmentCard(Patient patient) {
        this.patient = patient;
        this.therapy = patient.getCurrentTherapy();
        List<DayOfWeek> patientPillDays = therapy.getTreatmentCategory().getPillDays();
        this.isSundayDoseDate = patientPillDays.contains(DayOfWeek.Sunday);
        setTreatmentHistories();
        setTreatmentPausePeriods();
    }

    private void setTreatmentPausePeriods() {
        List<Treatment> allTreatments = new ArrayList(patient.getCurrentTherapy().getTreatments());
        allTreatments.add(patient.getCurrentTreatment());
        for (Treatment treatment : allTreatments) {
            for (TreatmentInterruption treatmentInterruption : treatment.getInterruptions())
                treatmentPausePeriods.add(new TreatmentPausePeriod(treatmentInterruption.getPauseDate(), treatmentInterruption.getResumptionDate()));
        }
    }

    private void setTreatmentHistories() {

        if (patient.getCurrentTherapy().getTreatments().isEmpty())
            return;

        List<Treatment> allTreatments = new ArrayList(patient.getCurrentTherapy().getTreatments());
        allTreatments.add(patient.getCurrentTreatment());
        for (Treatment treatment : allTreatments) {
            treatmentHistories.add(new TreatmentHistory(treatment.getProviderId(), treatment.getStartDate(), treatment.getEndDate()));
        }
    }

    public TreatmentCard initIPSection(List<Adherence> adherenceData) {
        LocalDate ipStartDate = therapy.getStartDate();
        ipAndEipAdherenceSection.init(patient, adherenceData, therapy, ipStartDate, ipBoxLastDoseDate(), asList(Phase.IP, Phase.EIP));
        return this;
    }

    public TreatmentCard initCPSection(List<Adherence> adherenceData) {
        LocalDate cpStartDate = therapy.getPhases().getCPStartDate();
        cpAdherenceSection.init(patient, adherenceData, therapy, cpStartDate, cpBoxLastDoseDate(), asList(Phase.CP));
        return this;
    }

    public List<MonthlyAdherence> getMonthlyAdherences() {
        return ipAndEipAdherenceSection.getMonthlyAdherences();
    }

    public LocalDate ipBoxAdherenceEndDate() {
        Phases phases = therapy.getPhases();
        LocalDate endDate = phases.getNextPhaseStartDate(Phase.IP);
        if (phases.ipPhaseWasExtended()) {
            endDate = phases.getNextPhaseStartDate(Phase.EIP);
        }
        return (endDate == null) ? today() : endDate.minusDays(1);
    }

    public LocalDate cpBoxAdherenceEndDate() {
        return today();
    }

    public LocalDate cpBoxAdherenceStartDate() {
        Phases phases = therapy.getPhases();
        return (phases.getCPStartDate() == null) ? today() : phases.getCPStartDate();
    }

    public LocalDate ipBoxLastDoseDate() {
        return therapy.getStartDate().plusMonths(MONTHS_IN_IP_BOX).minusDays(1);
    }

    public LocalDate cpBoxLastDoseDate() {
        Phases phases = therapy.getPhases();
        if (phases.hasBeenOnCp()) {
            return phases.getCPStartDate().plusMonths(MONTHS_IN_CP_BOX).minusDays(1);
        }
        return null;
    }

    public List<String> getProviderIds() {
        Set<String> all = new HashSet<String>();
        all.addAll(ipAndEipAdherenceSection.getProviderIds());
        all.addAll(cpAdherenceSection.getProviderIds());
        return new ArrayList<>(all);
    }

    public boolean isCPAdherenceSectionValid() {
        return null != patient.getCurrentTherapy().getPhases().getCPStartDate();
    }

    public boolean isIPAdherenceSectionValid() {
        return patient.getCurrentTherapy().isOrHasBeenOnIP();
    }
}
