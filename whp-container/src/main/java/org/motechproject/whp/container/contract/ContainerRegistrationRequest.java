package org.motechproject.whp.container.contract;

import lombok.Data;

@Data
public class ContainerRegistrationRequest {
    private String containerId;

    private String instance;

    private String providerId;

    private String channelId;
    private String callId;
    private String submitterId;
    private String submitterRole;

    public ContainerRegistrationRequest() {
    }

    public ContainerRegistrationRequest(String providerId, String containerId, String instance, String channelId, String callId) {
        this.providerId = providerId;
        this.containerId = containerId;
        this.instance = instance;
        this.channelId = channelId;
        this.callId = callId;
    }
}
