package org.motechproject.whp.it.remedi.inbound.request.provider;

import org.junit.Test;
import org.motechproject.whp.patient.command.UpdateScope;
import org.motechproject.whp.webservice.builder.ProviderRequestBuilder;
import org.motechproject.whp.webservice.request.ProviderWebRequest;

public class SecondaryMobileNumberIT extends BaseProviderIT {
    @Test
    public void shouldNotThrowExceptionWhenSecondaryMobileNumberIsEmpty() {
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("1a123").withDate("17/03/1990 17:03:56").withDistrict(VALID_DISTRICT).withSecondaryMobile("").build();
        validator.validate(providerWebRequest, UpdateScope.createScope); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldNotThrowExceptionWhenSecondaryMobileNumberIsNull() {
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("1a123").withDate("17/03/1990 17:03:56").withDistrict(VALID_DISTRICT).withSecondaryMobile(null).build();
        validator.validate(providerWebRequest, UpdateScope.createScope); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldThrowExceptionWhenSecondaryMobileNumberIsLessThan10Digits() {
        expectFieldValidationRuntimeException("field:secondary_mobile:Secondary mobile number should be empty or should have 10 digits");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("1a123").withDate("17/03/1990 17:03:56").withDistrict(VALID_DISTRICT).withSecondaryMobile("1234").build();
        validator.validate(providerWebRequest, UpdateScope.createScope); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldThrowExceptionWhenSecondaryMobileNumberIsMoreThan10Digits() {
        expectFieldValidationRuntimeException("field:secondary_mobile:Secondary mobile number should be empty or should have 10 digits");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("1a123").withDate("17/03/1990 17:03:56").withDistrict(VALID_DISTRICT).withSecondaryMobile("12345678901").build();
        validator.validate(providerWebRequest, UpdateScope.createScope); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldThrowExceptionWhenSecondaryMobileNumberIsNotNumeric() {
        expectFieldValidationRuntimeException("field:secondary_mobile:Secondary mobile number should be empty or should have 10 digits");
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("1a123").withDate("17/03/1990 17:03:56").withDistrict(VALID_DISTRICT).withSecondaryMobile("123456789a").build();
        validator.validate(providerWebRequest, UpdateScope.createScope); //Can be any scope. None of the validation is scope dependent.
    }

    @Test
    public void shouldNotThrowExceptionWhenSecondaryMobileNumberIs10Digits() {
        ProviderWebRequest providerWebRequest = new ProviderRequestBuilder().withProviderId("1a123").withDate("17/03/1990 17:03:56").withDistrict(VALID_DISTRICT).withSecondaryMobile("1234567890").build();
        validator.validate(providerWebRequest, UpdateScope.createScope); //Can be any scope. None of the validation is scope dependent.
    }
}
