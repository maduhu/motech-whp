package org.motechproject.whp.contract;

import lombok.Data;
import org.motechproject.validation.constraints.DateTimeFormat;
import org.motechproject.validation.constraints.NamedConstraint;
import org.motechproject.validation.constraints.NotNullOrEmpty;
import org.motechproject.whp.refdata.domain.WHPConstants;
import org.motechproject.whp.validation.APIKeyValidator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ProviderWebRequest {

    @NotNullOrEmpty
    private String provider_id;

    @NotNullOrEmpty
    @NamedConstraint(name = APIKeyValidator.API_KEY_VALIDATION)
    private String api_key;

    @NotNullOrEmpty
    private String district;

    @NotNullOrEmpty
    @Pattern(regexp = "^$|[0-9]{10}", message = "Mobile number should have 10 digits")
    private String primary_mobile;

    @Pattern(regexp = "^$|[0-9]{10}", message = "Secondary mobile number should be empty or should have 10 digits")
    private String secondary_mobile;

    @Pattern(regexp = "^$|[0-9]{10}", message = "Tertiary mobile number should be empty or should have 10 digits")
    private String tertiary_mobile;

    private String username;
    private String password;
    private String uuid;

    @NotNull
    @DateTimeFormat(pattern = WHPConstants.DATE_TIME_FORMAT)
    private String date;
}
