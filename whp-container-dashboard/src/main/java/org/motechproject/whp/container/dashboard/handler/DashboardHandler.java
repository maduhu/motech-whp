package org.motechproject.whp.container.dashboard.handler;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.annotations.MotechListener;
import org.motechproject.whp.container.WHPContainerConstants;
import org.motechproject.whp.container.dashboard.service.ContainerDashboardService;
import org.motechproject.whp.container.domain.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DashboardHandler {

    private ContainerDashboardService containerDashboardService;

    @Autowired
    public DashboardHandler(ContainerDashboardService containerDashboardService) {
        this.containerDashboardService = containerDashboardService;
    }

    @MotechListener(subjects = WHPContainerConstants.CONTAINER_ADDED_SUBJECT)
    public void onContainerAdded(MotechEvent event) {
        Container container = (Container) event.getParameters().get(WHPContainerConstants.CONTAINER_ADDED_CONTAINER);
        containerDashboardService.createDashboardRow(container);
    }
}