package org.motechproject.whp.service;

import org.motechproject.whp.common.domain.SputumTrackingInstance;
import org.motechproject.whp.containertracking.repository.AllContainerTrackingRecords;
import org.motechproject.whp.mapper.ContainerTrackingDashboardRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.whp.common.domain.SputumTrackingInstance.PreTreatment;

@Service
public class PreTreatmentContainerDashboardService extends ContainerDashboardService {

    @Override
    protected SputumTrackingInstance getSupportedInstance() {
        return PreTreatment;
    }

    @Autowired
    public PreTreatmentContainerDashboardService(AllContainerTrackingRecords allContainerTrackingRecords, ContainerTrackingDashboardRowMapper containerTrackingDashboardRowMapper) {
        super(allContainerTrackingRecords, containerTrackingDashboardRowMapper);
    }

    @Override
    public String entityName() {
        return "pre_treatment_container_tracking_dashboard_row";
    }
}
