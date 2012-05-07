package org.motechproject.whp.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.motechproject.validation.constraints.NamedConstraint;
import org.motechproject.whp.validation.APIKeyValidator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ProviderWebRequest {

    @NotEmpty
    private String provider_id;

    @NotNull
    @NamedConstraint(name = APIKeyValidator.API_KEY_VALIDATION)
    private String api_key;

    @NotEmpty
    private String district;

    @NotEmpty
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
    @DateTimeFormat(pattern = "dd/MM/YYYY HH:mm:ss")
    private String date;
}
