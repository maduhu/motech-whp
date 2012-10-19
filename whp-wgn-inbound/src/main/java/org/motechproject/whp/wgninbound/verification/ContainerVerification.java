package org.motechproject.whp.wgninbound.verification;

import org.motechproject.whp.common.exception.WHPError;
import org.motechproject.whp.common.validation.RequestValidator;
import org.motechproject.whp.wgninbound.request.ContainerVerificationRequest;
import org.motechproject.whp.wgninbound.request.ValidatorPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContainerVerification extends Verification<ContainerVerificationRequest> {

    @Autowired
    public ContainerVerification(RequestValidator validator, ValidatorPool validatorPool) {
        super(validator, validatorPool);
    }

    @Override
    protected List<WHPError> verify(ContainerVerificationRequest request) {
        List<WHPError> whpErrors = new ArrayList<>();
        validatorPool.verifyMobileNumber(request.getPhoneNumber(), whpErrors)
                     .verifyContainerMapping(request.getMsisdn(), request.getContainer_id(), whpErrors);
        return whpErrors;
    }
}
