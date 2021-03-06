package org.motechproject.whp.adherenceapi.adherence;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.whp.adherenceapi.domain.AdherenceCaptureStatus;
import org.motechproject.whp.adherenceapi.domain.ProviderId;
import org.motechproject.whp.adherenceapi.reporting.AdherenceCaptureReportRequest;
import org.motechproject.whp.adherenceapi.request.AdherenceNotCapturedRequest;
import org.motechproject.whp.adherenceapi.response.validation.AdherenceValidationResponse;
import org.motechproject.whp.adherenceapi.validator.AdherenceNotCapturedRequestValidator;
import org.motechproject.whp.reporting.service.ReportingPublisherService;
import org.motechproject.whp.reports.contract.AdherenceCaptureRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AdherenceNotCapturedOverIVRTest {


    @Mock
    private AdherenceNotCapturedRequestValidator adherenceNotCapturedRequestValidator;
    @Mock
    private ReportingPublisherService reportingService;
    private AdherenceNotCapturedOverIVR adherenceNotCapturedOverIVR;
    private ProviderId providerId;

    @Before
    public void setUp() {
        initMocks(this);
        adherenceNotCapturedOverIVR = new AdherenceNotCapturedOverIVR(reportingService, adherenceNotCapturedRequestValidator);
        initializeProvider();
    }

    private void initializeProvider() {
        providerId = new ProviderId("providerId");
    }

    @Test
    public void shouldReportAndReturnSuccessOnAdherenceNotCapturedOperation() {
        AdherenceNotCapturedRequest adherenceNotCapturedRequest = new AdherenceNotCapturedRequest("patientId");
        adherenceNotCapturedRequest.setIvrFileLength("20");
        adherenceNotCapturedRequest.setAdherenceCaptureStatus(AdherenceCaptureStatus.NO_INPUT.name());

        when(adherenceNotCapturedRequestValidator.validate(adherenceNotCapturedRequest.validationRequest(), providerId)).thenReturn(new AdherenceValidationResponse().success());

        assertEquals(new AdherenceValidationResponse().success(), adherenceNotCapturedOverIVR.recordNotCaptured(adherenceNotCapturedRequest, providerId));
        verify(reportingService).reportAdherenceCapture(new AdherenceCaptureReportRequest(adherenceNotCapturedRequest.validationRequest(), providerId, true, AdherenceCaptureStatus.NO_INPUT).request());
    }

    @Test
    public void shouldReturnFailureOnAdherenceNotCapturedOperationForValidationFailures() {
        AdherenceNotCapturedRequest adherenceNotCapturedRequest = new AdherenceNotCapturedRequest("patientId");

        when(adherenceNotCapturedRequestValidator.validate(adherenceNotCapturedRequest.validationRequest(), providerId)).thenReturn(new AdherenceValidationResponse().failure());

        assertEquals(new AdherenceValidationResponse().failure(), adherenceNotCapturedOverIVR.recordNotCaptured(adherenceNotCapturedRequest, providerId));
        verify(reportingService, never()).reportAdherenceCapture(any(AdherenceCaptureRequest.class));
    }
}
