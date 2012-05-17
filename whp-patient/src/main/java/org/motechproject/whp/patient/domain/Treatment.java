package org.motechproject.whp.patient.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.whp.patient.repository.ValidationErrors;
import org.motechproject.whp.refdata.domain.DiseaseClass;
import org.motechproject.whp.refdata.domain.TreatmentOutcome;
import org.motechproject.whp.refdata.domain.TreatmentStatus;
import org.springframework.util.CollectionUtils;

@Data
@TypeDiscriminator("doc.type == 'Treatment'")
public class Treatment extends MotechBaseDataObject {

    private Integer patientAge;
    private TreatmentCategory treatmentCategory;
    private LocalDate startDate;
    private DateTime creationDate;
    private LocalDate closeDate;
    private String tbRegistrationNumber;
    private TreatmentOutcome treatmentOutcome;
    private TreatmentStatus status = TreatmentStatus.Ongoing;
    private DiseaseClass diseaseClass;
    private SmearTestInstances smearTestInstances = new SmearTestInstances();
    private WeightInstances weightInstances = new WeightInstances();
    private TreatmentInterruptions interruptions = new TreatmentInterruptions();

    // Required for ektorp
    public Treatment() {
    }

    public Treatment(TreatmentCategory treatmentCategory, DiseaseClass diseaseClass, Integer patientAge) {
        this.treatmentCategory = treatmentCategory;
        this.diseaseClass = diseaseClass;
        this.patientAge = patientAge;
    }

    public void addSmearTestResult(SmearTestResults smearTestResults) {
        smearTestInstances.add(smearTestResults);
    }

    public void addWeightStatistics(WeightStatistics weightStatistics) {
        weightInstances.add(weightStatistics);
    }

    public void close(String treatmentOutcome, DateTime dateModified) {
        status = TreatmentStatus.Closed;
        closeDate = dateModified.toLocalDate();
        this.treatmentOutcome = TreatmentOutcome.valueOf(treatmentOutcome);
    }

    @JsonIgnore
    public boolean isValid(ValidationErrors validationErrors) {
        boolean isLatestSmearResultValid = true;
        boolean isLatestWeightStatisticValid = true;
        if(!CollectionUtils.isEmpty(smearTestInstances)) {
            isLatestSmearResultValid = smearTestInstances.latestResult().isValid(validationErrors);
        }
        if(!CollectionUtils.isEmpty(weightInstances)) {
            isLatestWeightStatisticValid = weightInstances.latestResult().isValid(validationErrors);
        }
        return isLatestSmearResultValid && isLatestWeightStatisticValid;
    }

    @JsonIgnore
    public boolean isClosed() {
        return status == TreatmentStatus.Closed;
    }

    public void pause(String reasonForPause, DateTime dateModified) {
        interruptions.add(new TreatmentInterruption(reasonForPause, dateModified.toLocalDate()));
    }

    public void resume(String reasonForResumption, DateTime dateModified) {
        interruptions.latestInterruption().resumeTreatment(reasonForResumption, dateModified.toLocalDate());
    }

    @JsonIgnore
    public boolean isPaused() {
        return !CollectionUtils.isEmpty(interruptions) && interruptions.latestInterruption().isCurrentlyPaused();
    }

}
