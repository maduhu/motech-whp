package org.motechproject.whp.controller;


import org.motechproject.whp.reponse.VerificationErrorResponse;
import org.motechproject.whp.reponse.VerificationResponse;
import org.motechproject.whp.wgninbound.request.ContainerVerificationRequest;
import org.motechproject.whp.wgninbound.request.ProviderVerificationRequest;
import org.motechproject.whp.wgninbound.response.VerificationResult;
import org.motechproject.whp.wgninbound.verification.ContainerVerification;
import org.motechproject.whp.wgninbound.verification.ProviderVerification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/sputumCall")
public class WGNVerificationController {

    private ProviderVerification providerVerification;
    private ContainerVerification containerVerification;

    @Autowired
    public WGNVerificationController(ProviderVerification providerVerification, ContainerVerification containerVerification) {
        this.providerVerification = providerVerification;
        this.containerVerification = containerVerification;
    }

    @RequestMapping(value = "provider/verify", method = RequestMethod.POST)
    @ResponseBody
    public VerificationResponse verifyProvider(@RequestBody ProviderVerificationRequest request, HttpServletResponse response) {
        response.setContentType("application/xml");
        VerificationResult verificationResult = providerVerification.verifyRequest(request);
        if (verificationResult.isSuccess()) {
            return new VerificationResponse(verificationResult);
        } else {
            return new VerificationErrorResponse(verificationResult);
        }
    }

    @RequestMapping(value = "container/verify", method = RequestMethod.POST)
    @ResponseBody
    public VerificationResponse verifyContainer(@RequestBody ContainerVerificationRequest request, HttpServletResponse response) {
        response.setContentType("application/xml");
        VerificationResult verificationResult = containerVerification.verifyRequest(request);
        if (verificationResult.isSuccess()) {
            return new VerificationResponse(verificationResult);
        } else {
            return new VerificationErrorResponse(verificationResult);
        }
    }
}
