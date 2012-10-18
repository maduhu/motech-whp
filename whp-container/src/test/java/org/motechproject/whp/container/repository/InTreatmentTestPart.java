package org.motechproject.whp.container.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.common.domain.ContainerStatus;
import org.motechproject.whp.common.domain.SputumTrackingInstance;
import org.motechproject.whp.container.builder.ContainerBuilder;
import org.motechproject.whp.container.domain.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.motechproject.whp.common.domain.ContainerStatus.Open;
import static org.motechproject.whp.common.domain.SmearTestResult.Positive;
import static org.motechproject.whp.common.domain.SputumTrackingInstance.EndIP;
import static org.motechproject.whp.common.domain.SputumTrackingInstance.InTreatment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/applicationContainerContext.xml")
public class InTreatmentTestPart extends AllContainerTrackingRecordsTestPart {

    @Autowired
    AllContainerTrackingRecords allContainerTrackingRecords;

    @After
    public void tearDown() {
        allContainerTrackingRecords.removeAll();
    }

    @Test
    public void shouldFilterInTreatmentContainerRecordsByInstanceAndProviderId() {
        Properties queryParams = new Properties();
        String providerId = "providerId";
        queryParams.put("providerId", providerId);
        queryParams.put("containerInstance", "InTreatment");
        SputumTrackingInstance inTreatment = InTreatment;
        String districtName = "East Champaran";

        Container expectedContainerTrackingRecord = createContainerTrackingRecord(providerId, districtName, inTreatment);

        allContainerTrackingRecords.add(expectedContainerTrackingRecord);
        allContainerTrackingRecords.add(createContainerTrackingRecord(providerId, districtName, SputumTrackingInstance.EndTreatment));
        allContainerTrackingRecords.add(createContainerTrackingRecord("anotherProvider", districtName, InTreatment));

        int skip = 0;
        int limit = 10;

        List<Container> results = allContainerTrackingRecords.filter(InTreatment, queryParams, skip, limit);

        assertThat(results.size(), is(1));
        assertEquals(results.get(0).getId(), expectedContainerTrackingRecord.getId());
    }

    @Test
    public void shouldFilterInTreatmentContainerRecordsByAllFilterCriteria() {
        Properties queryParams = new Properties();
        String providerId = "providerId";
        String districtName = "Begusarai";
        queryParams.put("providerId", providerId);
        queryParams.put("district", districtName);
        queryParams.put("cumulativeResult", Positive.name());
        queryParams.put("containerStatus", Open.name());
        queryParams.put("containerIssuedDateFrom", "01/02/2010");
        queryParams.put("containerIssuedDateTo", "30/04/2010");
        queryParams.put("reasonForClosure", "reasonForClosure");
        queryParams.put("mappingInstance", EndIP.name());

        Container expectedContainerTrackingRecord = new ContainerBuilder()
                .withProviderId(providerId)
                .withInstance(InTreatment)
                .withProviderDistrict(districtName)
                .withCumulativeResult(Positive)
                .withStatus(Open)
                .withContainerIssuedDate(DateUtil.newDate(2010, 2, 5))
                .withReasonForClosure("reasonForClosure")
                .withMappingInstance(EndIP)
                .build();

        Container containerTrackingRecord1 = new ContainerBuilder()
                .withProviderId(providerId)
                .withInstance(InTreatment)
                .withProviderDistrict(districtName)
                .withCumulativeResult(Positive)
                .withStatus(ContainerStatus.Closed)
                .withContainerIssuedDate(DateUtil.newDate(2010, 2, 5))
                .build();

        Container containerTrackingRecord2 = new ContainerBuilder()
                .withProviderId(providerId)
                .withInstance(InTreatment)
                .withProviderDistrict(districtName)
                .withCumulativeResult(Positive)
                .withStatus(Open)
                .withContainerIssuedDate(DateUtil.newDate(2009, 2, 5))
                .build();

        allContainerTrackingRecords.add(expectedContainerTrackingRecord);
        allContainerTrackingRecords.add(containerTrackingRecord1);
        allContainerTrackingRecords.add(containerTrackingRecord2);

        int skip = 0;
        int limit = 10;

        List<Container> results = allContainerTrackingRecords.filter(InTreatment, queryParams, skip, limit);

        assertThat(results.size(), is(1));
        assertThat(results.get(0).getId(), is(expectedContainerTrackingRecord.getId()));
    }

    @Test
    public void shouldCountInTreatmentContainerTrackingRecordRows() {
        String providerId1 = "providerId";
        String providerId2 = "anotherProvider";
        String districtName1 = "East Champaran";

        allContainerTrackingRecords.add(createContainerTrackingRecord(providerId1, districtName1, InTreatment));
        allContainerTrackingRecords.add(createContainerTrackingRecord(providerId1, districtName1, InTreatment));
        allContainerTrackingRecords.add(createContainerTrackingRecord(providerId1, districtName1, SputumTrackingInstance.PreTreatment));
        allContainerTrackingRecords.add(createContainerTrackingRecord(providerId2, districtName1, InTreatment));

        // Assertion 1 for provider1-Instance1
        Properties queryParams = new Properties();
        queryParams.put("providerId", providerId1);
        int recordCount = allContainerTrackingRecords.count(InTreatment, queryParams);
        assertEquals(2, recordCount);


        // Assertion 3 for provider2-Instance1
        queryParams = new Properties();
        queryParams.put("providerId", providerId2);
        recordCount = allContainerTrackingRecords.count(InTreatment, queryParams);
        assertEquals(1, recordCount);
    }
}