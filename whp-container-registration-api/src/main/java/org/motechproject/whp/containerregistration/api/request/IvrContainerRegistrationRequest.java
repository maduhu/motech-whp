package org.motechproject.whp.containerregistration.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.motechproject.validation.constraints.NotNullOrEmpty;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "register_container")
@Data
@EqualsAndHashCode
public class IvrContainerRegistrationRequest extends VerificationRequest {

    @NotNullOrEmpty
    @Size(min = 10, message = "should be atleast 10 digits in length")
    private String msisdn;

    @NotNullOrEmpty
    @DecimalMax("99999")
    @DecimalMin("10000")
    private String container_id;

    @NotNullOrEmpty
    private String phase;

    @NotNullOrEmpty
    private String call_id;

    //for Spring MVC
    public IvrContainerRegistrationRequest() {
    }

    public IvrContainerRegistrationRequest(String msisdn, String container_id, String call_id, String phase) {
        super(msisdn);
        this.msisdn = msisdn;
        this.call_id = call_id;
        this.container_id = container_id;
        this.phase = phase;
    }

    @XmlElement(name = "msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @XmlElement(name = "call_id")
    public String getCall_id() {
        return call_id;
    }

    @XmlElement(name = "container_id")
    public String getContainer_id() {
        return container_id;
    }

    public void setMsisdn(String msisdn) {
        super.setMsisdn(msisdn);
        this.msisdn = msisdn;
    }
}

