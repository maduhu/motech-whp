package org.motechproject.whp.containerregistration.api.request;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.motechproject.validation.constraints.NotNullOrEmpty;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "validate_container_id")
@EqualsAndHashCode
@Setter
public class ContainerVerificationRequest extends VerificationRequest {

    @NotNullOrEmpty
    @Size(min = 10, message = "should be atleast 10 digits in length")
    private String msisdn;

    @NotNullOrEmpty
    private String call_id;

    private String container_id;

    /*Required for spring mvc*/
    public ContainerVerificationRequest() {
    }

    public ContainerVerificationRequest(String msisdn, String container_id, String call_id) {
        super(msisdn);
        this.msisdn = msisdn;
        this.call_id = call_id;
        this.container_id = container_id;
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
