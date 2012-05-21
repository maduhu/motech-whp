package org.motechproject.whp.patient.domain;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;
import org.motechproject.whp.patient.exception.errorcode.WHPDomainErrorCode;
import org.motechproject.whp.refdata.domain.WeightInstance;

import java.util.ArrayList;
import java.util.List;

@Data
public class WeightStatistics {

    private WeightInstance weight_instance;
    private Double weight;
    private LocalDate measuringDate;

    public WeightStatistics() {
    }

    public WeightStatistics(WeightInstance weightInstance, Double weight, LocalDate measuringDate) {
        this.weight_instance = weightInstance;
        this.weight = weight;
        this.measuringDate = measuringDate;
    }

    @JsonIgnore
    public boolean isValid(List<WHPDomainErrorCode> validationErrors) {
        boolean isFilled = weight_instance != null && weight != null && measuringDate != null;
        if (!isFilled) {
            validationErrors.add(WHPDomainErrorCode.NULL_VALUE_IN_WEIGHT_STATISTICS);
        }
        return isFilled;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return weight_instance == null && weight == null;
    }

    @JsonIgnore
    public boolean isOfInstance(WeightInstance weightInstance) {
        return this.weight_instance.equals(weightInstance);
    }
}
