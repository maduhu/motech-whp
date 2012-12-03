package org.motechproject.whp.it.remedi.inbound.request.patient;

import org.junit.Test;
import org.motechproject.whp.common.exception.WHPRuntimeException;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.patient.command.UpdateScope;
import org.motechproject.whp.user.domain.Provider;
import org.motechproject.whp.webservice.builder.PatientWebRequestBuilder;
import org.motechproject.whp.webservice.request.PatientWebRequest;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class ProviderIdIT extends BasePatientIT {

    @Test
    public void shouldThrowExceptionWhenProviderIdIsNotFound() {
        expectFieldValidationRuntimeException("No provider is found with id:nonExistantProviderId");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId("nonExistantProviderId").build();
        validator.validate(webRequest, UpdateScope.createScope);
    }

    @Test
    public void shouldThrowSingleExceptionWhenProviderIdIsNull() {
        String errorMessage = "";
        try {
            PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId(null).build();
            validator.validate(webRequest, UpdateScope.createScope);
        } catch (WHPRuntimeException e) {
            if (e.getMessage().contains("field:provider_id:may not be null")) {
                fail("Not Null validation is not required.");
            }
            errorMessage = e.getMessage();
        }
        assertTrue(errorMessage.contains("field:provider_id:Provider Id cannot be null"));
    }

    @Test
    public void shouldNotThrowExceptionWhenProviderIdIsFound() {
        Provider defaultProvider = new Provider("providerId", "1231231231", "chambal", DateUtil.now());
        allProviders.add(defaultProvider);
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withProviderId("providerId").build();
        validator.validate(webRequest, UpdateScope.createScope);
    }

}
