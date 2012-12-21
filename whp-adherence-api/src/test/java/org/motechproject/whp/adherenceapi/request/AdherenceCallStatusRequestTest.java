package org.motechproject.whp.adherenceapi.request;

import org.hibernate.validator.HibernateValidator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static javax.xml.bind.JAXBContext.newInstance;
import static junit.framework.Assert.assertTrue;

public class AdherenceCallStatusRequestTest {

    private LocalValidatorFactoryBean localValidatorFactory;
    private Marshaller marshaller;

    @Before
    public void setup() throws JAXBException {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
        initializeJaxb();
    }

    private void initializeJaxb() throws JAXBException {
        JAXBContext context = newInstance(AdherenceCallStatusRequest.class);
        marshaller = context.createMarshaller();
    }

    @Test
    public void shouldBeInvalidWhenStartTimeHasInvalidFormat() {
        AdherenceCallStatusRequest request = validRequest();
        request.setStartTime("invalidTime");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("invalid date format"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("startTime"));
    }

    @Test
    public void shouldBeInvalidWhenEndTimeHasInvalidFormat() {
        AdherenceCallStatusRequest request = validRequest();
        request.setEndTime("invalidTime");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("invalid date format"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("endTime"));
    }

    @Test
    public void shouldBeInvalidWhenAttemptTimeHasInvalidFormat() {
        AdherenceCallStatusRequest request = validRequest();
        request.setAttemptTime("invalidTime");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("invalid date format"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("attemptTime"));
    }

    @Test
    public void shouldBeInvalidIfCallStatusIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setCallStatus("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("callStatus"));
    }

    @Test
    public void shouldBeInvalidIfProviderIdIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setProviderId("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("providerId"));
    }

    @Test
    public void shouldBeInvalidIfAdherenceCapturedCountIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setAdherenceCapturedCount("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("adherenceCapturedCount"));
    }

    @Test
    public void shouldBeInvalidIfMsidnIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setMsisdn("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("msisdn"));
    }

    @Test
    public void shouldBeInvalidIfMsisdnIsLessThanTenDigitsLong() {
        AdherenceCallStatusRequest request = validRequest();
        request.setMsisdn("123456789");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("length must be between 10 and 2147483647"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("msisdn"));
    }

    @Test
    public void shouldBeInvalidIfDisconnectionTypeIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setDisconnectionType("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("disconnectionType"));
    }

    @Test
    public void shouldBeInvalidWhenFlashingCallIdIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setFlashingCallId("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("flashingCallId"));
    }

    @Test
    public void shouldBeInvalidWhenCallAnsweredIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setCallAnswered("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("callAnswered"));
    }

    @Test
    public void shouldBeInvalidWhenAdherenceNotCapturedCountIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setAdherenceNotCapturedCount("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("adherenceNotCapturedCount"));
    }

    @Test
    public void shouldBeInvalidIfPatientCountIsEmpty() {
        AdherenceCallStatusRequest request = validRequest();
        request.setPatientCount("");

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getMessage()).contains("may not be empty"));
        assertTrue(extract(constraintViolations, on(ConstraintViolation.class).getPropertyPath().toString()).contains("patientCount"));
    }

    @Test
    public void shouldBeValidWhenAllTheFieldsAreValid() {
        AdherenceCallStatusRequest request = validRequest();

        List<ConstraintViolation<AdherenceCallStatusRequest>> constraintViolations = new ArrayList<>(localValidatorFactory.validate(request));
        assertTrue(constraintViolations.isEmpty());
    }

    private AdherenceCallStatusRequest validRequest() {
        AdherenceCallStatusRequest request = new AdherenceCallStatusRequest();
        request.setCallId("callId");
        request.setMsisdn("1234567890");
        request.setStartTime("12/12/2011 11:11:11");
        request.setEndTime("12/12/2011 11:11:11");
        request.setAdherenceCapturedCount("1");
        request.setAttemptTime("12/12/2011 11:11:11");
        request.setCallAnswered("YES");
        request.setCallStatus("SUCCESS");
        request.setDisconnectionType("PROVIDER_HANGUP");
        request.setFlashingCallId("flashingCallId");
        request.setAdherenceNotCapturedCount("0");
        request.setPatientCount("1");
        request.setProviderId("providerId");
        return request;
    }
}
