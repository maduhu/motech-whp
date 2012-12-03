package org.motechproject.whp.it.remedi.inbound.request.patient;

import org.junit.Test;
import org.motechproject.whp.patient.command.UpdateScope;
import org.motechproject.whp.webservice.builder.PatientWebRequestBuilder;
import org.motechproject.whp.webservice.request.PatientWebRequest;

public class LastModifiedDateIT extends BasePatientIT {
    @Test
    public void shouldNotThrowException_WhenLastModifiedDateFormatIsCorrect() {
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03/04/2012 02:20:30").build();
        validator.validate(webRequest, UpdateScope.createScope);
    }

    @Test
    public void shouldThrowException_WhenLastModifiedDateFormatIsNotTheCorrectDateTimeFormat() {
        expectFieldValidationRuntimeException("03-04-2012\" is malformed at \"-04-2012");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03-04-2012").build();
        validator.validate(webRequest, UpdateScope.createScope);
    }

    @Test
    public void shouldThrowException_WhenLastModifiedDateFormatDoesNotHaveTimeComponent() {
        expectFieldValidationRuntimeException("field:date_modified:Invalid format: \"03/04/2012\" is too short");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("03/04/2012").build();
        validator.validate(webRequest, UpdateScope.createScope);
    }

    @Test
    public void shouldThrowAnExceptionIfLastModifiedDateIsEmpty() {
        expectFieldValidationRuntimeException("field:date_modified:Invalid format: \"\"");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate("").build();
        validator.validate(webRequest, UpdateScope.createScope);
    }

    @Test
    public void shouldThrowExceptionWhenLastModifiedDateFormatIsNull() {
        expectFieldValidationRuntimeException("field:date_modified:value should not be null");
        PatientWebRequest webRequest = new PatientWebRequestBuilder().withDefaults().withLastModifiedDate(null).build();
        validator.validate(webRequest, UpdateScope.createScope);
    }
}
