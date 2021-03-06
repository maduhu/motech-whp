package org.motechproject.whp.it.adherence.capture.audit.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.util.DateUtil;
import org.motechproject.whp.adherence.audit.domain.AdherenceAuditLog;
import org.motechproject.whp.adherence.audit.domain.AuditLog;
import org.motechproject.whp.adherence.audit.domain.DailyAdherenceAuditLog;
import org.motechproject.whp.adherence.audit.repository.AllAdherenceAuditLogs;
import org.motechproject.whp.adherence.audit.repository.AllDailyAdherenceAuditLogs;
import org.motechproject.whp.adherence.audit.repository.AllWeeklyAdherenceAuditLogs;
import org.motechproject.whp.adherence.domain.AdherenceSource;
import org.motechproject.whp.adherence.domain.PillStatus;
import org.motechproject.whp.it.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@ContextConfiguration(locations = "classpath*:/applicationITContext.xml")
public class AllAdherenceAuditLogsIT extends SpringIntegrationTest {

    @Autowired
    AllWeeklyAdherenceAuditLogs allWeeklyAdherenceAuditLogs;
    @Autowired
    AllDailyAdherenceAuditLogs allDailyAdherenceAuditLogs;

    @Autowired
    AllAdherenceAuditLogs allAdherenceAuditLogs;

    AuditLog weeklyAuditLog1 = new AuditLog(DateUtil.now(), 2, "remark1", AdherenceSource.IVR.name(), "patient1", "tbId1", "providerId1", "providerId1");
    AuditLog weeklyAuditLog2 = new AuditLog(DateUtil.now(), 3, "remark1", AdherenceSource.WEB.name(), "patient2", "tbId2", "providerId2", "providerId2");
    AuditLog weeklyAuditLog3 = new AuditLog(DateUtil.now(), 2, "remark1", AdherenceSource.IVR.name(), "patient1", "tbId1", "providerId1", "providerId1");

    private final String providerId = "providerId";
    //set up daily audit logs
    DailyAdherenceAuditLog dailyAuditLog0 = new DailyAdherenceAuditLog("patient1", "tbId1", DateUtil.today(), PillStatus.Taken, "cmfAdmin", AdherenceSource.WEB.name(), DateUtil.now().minusMonths(3), providerId);
    DailyAdherenceAuditLog dailyAuditLog1 = new DailyAdherenceAuditLog("patient1", "tbId1", DateUtil.today(), PillStatus.Taken, "cmfAdmin", AdherenceSource.WEB.name(), DateUtil.now(), providerId);
    DailyAdherenceAuditLog dailyAuditLog2 = new DailyAdherenceAuditLog("patient2", "tbId2", DateUtil.today(), PillStatus.NotTaken, "cmfAdmin", AdherenceSource.WEB.name(), DateUtil.now(), providerId);
    DailyAdherenceAuditLog dailyAuditLog3 = new DailyAdherenceAuditLog("patient1", "tbId1", DateUtil.today(), PillStatus.Unknown, "cmfAdmin", AdherenceSource.WEB.name(), DateUtil.now(), providerId);

    @Test
    public void shouldGetAllAdherenceLogs() {
        setUpWeeklyAuditLogs();
        setUpDailyAuditLogs();

        List<AdherenceAuditLog> adherenceAuditLogList = allAdherenceAuditLogs.allLogs(0, 10);
        assertThat(adherenceAuditLogList.size(), is(7));

        List<AdherenceAuditLog> firstPageAdherenceAuditLogList = allAdherenceAuditLogs.allLogs(0, 4);
        assertThat(firstPageAdherenceAuditLogList.size(), is(4));

        List<AdherenceAuditLog> secondPageAdherenceAuditLogList = allAdherenceAuditLogs.allLogs(1, 4);
        assertThat(secondPageAdherenceAuditLogList.size(), is(3));
    }

    private void setUpDailyAuditLogs() {
        allDailyAdherenceAuditLogs.add(dailyAuditLog0);
        allDailyAdherenceAuditLogs.add(dailyAuditLog1);
        allDailyAdherenceAuditLogs.add(dailyAuditLog2);
        allDailyAdherenceAuditLogs.add(dailyAuditLog3);
    }

    private void setUpWeeklyAuditLogs() {
        allWeeklyAdherenceAuditLogs.add(weeklyAuditLog1);
        allWeeklyAdherenceAuditLogs.add(weeklyAuditLog2);
        allWeeklyAdherenceAuditLogs.add(weeklyAuditLog3);
    }

    @Before
    @After
    public void setUpAndTearDown() {
        allWeeklyAdherenceAuditLogs.removeAll();
        allDailyAdherenceAuditLogs.removeAll();
    }
}
