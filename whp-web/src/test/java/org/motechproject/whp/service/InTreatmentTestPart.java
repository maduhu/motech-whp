package org.motechproject.whp.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.paginator.response.PageResults;
import org.motechproject.whp.container.tracking.builder.ContainerTrackingRecordBuilder;
import org.motechproject.whp.container.tracking.model.ContainerTrackingRecord;
import org.motechproject.whp.container.tracking.repository.AllInTreatmentContainerTrackingRecordsImpl;
import org.motechproject.whp.mapper.ContainerTrackingDashboardRowMapper;
import org.motechproject.whp.uimodel.ContainerTrackingDashboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.whp.common.domain.SputumTrackingInstance.InTreatment;

public class InTreatmentTestPart {
    InTreatmentContainerDashboardService inTreatmentContainerDashboardService;
    @Mock
    private ContainerTrackingDashboardRowMapper containerTrackingDashboardRowMapper;

    @Mock
    private AllInTreatmentContainerTrackingRecordsImpl allInTreatmentContainerTrackingRecords;

    @Before
    public void setup() {
        initMocks(this);
        inTreatmentContainerDashboardService = new InTreatmentContainerDashboardService(allInTreatmentContainerTrackingRecords, containerTrackingDashboardRowMapper);
    }

    @Test
    public void shouldReturnEntityNameToBePaginatedAsSputumTrackingDashboardRow() {
        assertEquals("in_treatment_container_tracking_dashboard_row", inTreatmentContainerDashboardService.entityName());
    }

    @Test
    public void shouldFilterContainerTrackingRecordsForGivenFilterCriteria() {
        ContainerTrackingRecord containerTrackingRecord1 = new ContainerTrackingRecordBuilder().withDefaults().withInstance(InTreatment).build();
        ContainerTrackingRecord containerTrackingRecord2 = new ContainerTrackingRecordBuilder().withDefaults().withInstance(InTreatment).build();
        List<ContainerTrackingRecord> results = new ArrayList<>();
        results.add(containerTrackingRecord1);
        results.add(containerTrackingRecord2);

        ContainerTrackingDashboardRow expectedPageResult1 = new ContainerTrackingDashboardRow();
        ContainerTrackingDashboardRow expectedPageResult2 = new ContainerTrackingDashboardRow();
        when(containerTrackingDashboardRowMapper.mapFrom(containerTrackingRecord1)).thenReturn(expectedPageResult1);
        when(containerTrackingDashboardRowMapper.mapFrom(containerTrackingRecord2)).thenReturn(expectedPageResult2);

        Properties filterParams = new Properties();
        int skip = 0;
        int limit = 10;

        when(allInTreatmentContainerTrackingRecords.count(filterParams)).thenReturn(2);
        when(allInTreatmentContainerTrackingRecords.filter(filterParams, skip, limit)).thenReturn(results);

        PageResults<ContainerTrackingDashboardRow> pageResults = inTreatmentContainerDashboardService.page(1, limit, filterParams);

        verify(allInTreatmentContainerTrackingRecords).filter(filterParams, skip, limit);
        verify(allInTreatmentContainerTrackingRecords).count(filterParams);

        assertEquals(new Integer(2), pageResults.getTotalRows());
        assertEquals(2, pageResults.getResults().size());
        assertEquals(expectedPageResult1, pageResults.getResults().get(0));
        assertEquals(expectedPageResult2, pageResults.getResults().get(1));
    }

}
