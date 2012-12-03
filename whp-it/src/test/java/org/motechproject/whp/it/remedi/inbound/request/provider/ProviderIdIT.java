package org.motechproject.whp.it.remedi.inbound.request.provider;

import org.junit.Test;
import org.motechproject.whp.patient.command.UpdateScope;
import org.motechproject.whp.webservice.builder.ProviderRequestBuilder;
import org.motechproject.whp.webservice.request.ProviderWebRequest;

public class ProviderIdIT extends BaseProviderIT {

    @Test
    public void shouldThrowAnExceptionIfProviderIdIsNull() {
        expectFieldValidationRuntimeException("field:provider_id:value should not be null");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId(null).withDate("17/03/1990 17:03:56").withDistrict("Chambal").withPrimaryMobile("9880000000").build();
        validator.validate(providerWebRequest, UpdateScope.createScope); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldThrowAnExceptionIfProviderIdIsEmpty() {
        expectFieldValidationRuntimeException("field:provider_id:value should not be null");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("").withDate("17/03/1990 17:03:56").withDistrict("Chambal").withPrimaryMobile("9880000000").build();
        validator.validate(providerWebRequest, UpdateScope.createScope); //Can be any scope. None of the validation is scope dependent.
    }

}
