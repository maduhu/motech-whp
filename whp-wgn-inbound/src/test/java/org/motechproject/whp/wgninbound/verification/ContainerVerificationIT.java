package org.motechproject.whp.wgninbound.verification;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.motechproject.whp.common.exception.WHPError;
import org.motechproject.whp.common.exception.WHPErrors;
import org.motechproject.whp.wgninbound.request.ContainerVerificationRequest;
import org.motechproject.whp.wgninbound.request.ValidatorPool;
import org.motechproject.whp.wgninbound.response.VerificationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = "classpath:applicationWHPWgnInputContext.xml")
public class ContainerVerificationIT {

    @Autowired
    ContainerVerification containerVerification;

    @Autowired
    @ReplaceWithMock
    private ValidatorPool validatorPool;

    @Captor
    ArgumentCaptor<WHPErrors> whpErrors;

    @Before
    public void setup() {
        reset(validatorPool);
        initMocks(this);
    }

    @Test
    public void shouldReturnFailureWhenMSISDNIsEmpty() {
        String emptyMSISDN = "";
        String containerId = "containerId";

        VerificationResult result = containerVerification.verifyRequest(new ContainerVerificationRequest(emptyMSISDN, containerId, "callId"));

        assertTrue(result.isError());
    }

    @Test
    public void shouldReturnErrorOnMSIDNGreaterThanTenDigitsInLength() {
        ContainerVerificationRequest request = new ContainerVerificationRequest();
        request.setMsisdn("1234");
        request.setCall_id("callId");
        request.setContainer_id("containerId");

        VerificationResult result = containerVerification.verifyRequest(request);
        assertTrue(errorContains("field:msisdn:should be atleast 10 digits in length", result.getErrors()));
    }

    @Test
    public void shouldReturnFailureWhenCallIdIsEmpty() {
        String mobileNumber = "1234567890";
        String containerId = "containerId";
        String emptyCallId = "";

        VerificationResult result = containerVerification.verifyRequest(new ContainerVerificationRequest(mobileNumber, containerId, emptyCallId));
        assertTrue(result.isError());
    }

    @Test
    public void shouldReturnFailureWhenContainerIdIsEmpty() {
        String mobileNumber = "1234567890";
        String emptyContainerId = "";

        VerificationResult result = containerVerification.verifyRequest(new ContainerVerificationRequest(mobileNumber, emptyContainerId, "callId"));

        assertTrue(result.isError());
    }

    @Test
    public void shouldVerifyRequest() {
        String msisdn = "1234567890";
        String callId = "callId";
        String containerId = "containerId";
        ContainerVerificationRequest request = new ContainerVerificationRequest(msisdn, containerId, callId);

        when(validatorPool.verifyMobileNumber(eq(msisdn), whpErrors.capture())).thenReturn(validatorPool);
        when(validatorPool.verifyContainerMapping(eq(msisdn), eq(containerId), whpErrors.capture())).thenReturn(validatorPool);

        List<WHPError> errors = containerVerification.verify(request);

        verify(validatorPool).verifyMobileNumber(eq(msisdn), whpErrors.capture());
        verify(validatorPool).verifyContainerMapping(eq(msisdn), eq(containerId), whpErrors.capture());
        Assert.assertTrue(errors.isEmpty());
    }

    private boolean errorContains(String expectedMessage, List<WHPError> errors) {
        boolean found = false;
        for (WHPError error : errors) {
            found |= StringUtils.equals(expectedMessage, error.getMessage());
        }
        return found;
    }
}
