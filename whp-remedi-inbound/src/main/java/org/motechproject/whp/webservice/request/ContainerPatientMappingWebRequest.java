package org.motechproject.whp.webservice.request;

import lombok.Data;
import org.motechproject.casexml.contract.CaseXmlRequest;
import org.motechproject.validation.constraints.DateTimeFormat;
import org.motechproject.validation.constraints.Enumeration;
import org.motechproject.validation.constraints.NamedConstraint;
import org.motechproject.validation.constraints.NotNullOrEmpty;
import org.motechproject.whp.common.domain.SputumTrackingInstance;
import org.motechproject.whp.webservice.validation.APIKeyValidator;

import javax.validation.constraints.Pattern;

import static org.motechproject.whp.common.util.WHPDate.DATE_TIME_FORMAT;

@Data
public class ContainerPatientMappingWebRequest implements CaseXmlRequest {
    private static final String EMPTY_STRING = "";

    @NotNullOrEmpty
    private String case_id;

    @NamedConstraint(name = APIKeyValidator.API_KEY_VALIDATION)
    private String api_key;

    @NotNullOrEmpty
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    private String date_modified;

    private String tb_registration_date;

    @NotNullOrEmpty
    @Pattern(regexp = "patient_mapping")
    private String update_type;

    private String patient_id;

    private String tb_id;

    @Enumeration(type = SputumTrackingInstance.class, validateEmptyString = false)
    private String smear_sample_instance;

    public boolean isMappingRequest() {
        return !(getPatient_id().equals(EMPTY_STRING)
                && getTb_id().equals(EMPTY_STRING)
                && getSmear_sample_instance().equals(EMPTY_STRING));
    }

    @Override
    public String getId() {
        return case_id;
    }

    @Override
    public String getType() {
        return "Container Patient Mapping";
    }
}
