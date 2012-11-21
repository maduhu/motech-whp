package org.motechproject.whp.webservice.request;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.motechproject.casexml.contract.CaseXmlRequest;
import org.motechproject.validation.constraints.DateTimeFormat;
import org.motechproject.validation.constraints.Enumeration;
import org.motechproject.validation.constraints.NamedConstraint;
import org.motechproject.validation.constraints.NotNullOrEmpty;
import org.motechproject.whp.webservice.validation.APIKeyValidator;
import org.motechproject.whp.common.domain.SmearTestResult;

import javax.validation.constraints.Pattern;

import static org.motechproject.whp.common.util.WHPDate.DATE_FORMAT;
import static org.motechproject.whp.common.util.WHPDate.DATE_TIME_FORMAT;

@Data
public class SputumLabResultsWebRequest implements CaseXmlRequest {

    @NotNullOrEmpty
    private String case_id;

    @NamedConstraint(name = APIKeyValidator.API_KEY_VALIDATION)
    private String api_key;

    @NotNullOrEmpty
    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    private String date_modified;


    @NotNullOrEmpty
    @Pattern(regexp = "lab_results")
    private String update_type;

    @DateTimeFormat(pattern = DATE_FORMAT)
    private String smear_test_date_1;

    @Enumeration(type = SmearTestResult.class)
    private String smear_test_result_1;

    @DateTimeFormat(pattern = DATE_FORMAT)
    private String smear_test_date_2;

    @Enumeration(type = SmearTestResult.class)
    private String smear_test_result_2;

    private String lab_name;

    private String lab_number;

    public boolean hasCompleteLabResults() {
        if (StringUtils.isNotEmpty(getSmear_test_date_1())
                && StringUtils.isNotEmpty(getSmear_test_date_2())
                && StringUtils.isNotEmpty(getSmear_test_result_1())
                && StringUtils.isNotEmpty(getSmear_test_result_2())) {
            return true;
        }
        return false;
    }

    @Override
    public String getId() {
        return case_id;
    }

    @Override
    public String getType() {
        return "Sputum Lab Results";
    }
}
