package org.motechproject.whp.providerreminder.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.whp.common.event.EventKeys;
import org.motechproject.whp.providerreminder.configuration.ProviderReminderConfiguration;
import org.motechproject.whp.providerreminder.service.ProviderReminderScheduler;

import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.today;
import static org.motechproject.whp.common.event.EventKeys.ADHERENCE_WINDOW_APPROACHING_EVENT_NAME;
import static org.motechproject.whp.providerreminder.domain.ProviderReminderType.ADHERENCE_WINDOW_APPROACHING;

public class ProviderReminderSchedulerTest extends BaseUnitTest{
    @Mock
    private MotechSchedulerService motechSchedulerService;

    private ProviderReminderScheduler providerReminderScheduler;

    @Before
    public void setUp() {
        initMocks(this);
        providerReminderScheduler = new ProviderReminderScheduler(motechSchedulerService);
    }

    @Test
    public void shouldScheduleAJob() {
        DayOfWeek dayOfWeek = DayOfWeek.Sunday;
        ProviderReminderConfiguration providerReminderConfiguration = createProviderReminderConfiguration("minutes", "hour", dayOfWeek);

        providerReminderScheduler.scheduleJob(providerReminderConfiguration);

        ArgumentCaptor<CronSchedulableJob> captor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(captor.capture());
        CronSchedulableJob job = captor.getValue();

        assertEquals(ADHERENCE_WINDOW_APPROACHING_EVENT_NAME, job.getMotechEvent().getSubject());
        assertEquals(ADHERENCE_WINDOW_APPROACHING.name(), job.getMotechEvent().getParameters().get(MotechSchedulerService.JOB_ID_KEY));
        assertEquals("0 minutes hour ? * " + dayOfWeek.getShortName(), job.getCronExpression());
    }

    private ProviderReminderConfiguration createProviderReminderConfiguration(String minutes, String hour, DayOfWeek dayOfWeek) {
        ProviderReminderConfiguration providerReminderConfiguration = new ProviderReminderConfiguration();
        providerReminderConfiguration.setMinutes(minutes);
        providerReminderConfiguration.setHour(hour);
        providerReminderConfiguration.setDayOfWeek(dayOfWeek);
        providerReminderConfiguration.setReminderType(ADHERENCE_WINDOW_APPROACHING);
        return providerReminderConfiguration;
    }

    @Test
    public void shouldReturnNextScheduleForAJob() {
        String subject = ADHERENCE_WINDOW_APPROACHING_EVENT_NAME;
        String jobId = ADHERENCE_WINDOW_APPROACHING.name();

        mockCurrentDate(today());

        Date fromDate = today().toDate();
        Date toDate = new LocalDate(today()).plusDays(7).toDate();

        Date expectedNextFireTime = today().plusDays(2).toDate();

        when(motechSchedulerService.getScheduledJobTimings(subject, jobId, fromDate, toDate)).thenReturn(asList(expectedNextFireTime));
        assertEquals(expectedNextFireTime, providerReminderScheduler.getNextFireTime(ADHERENCE_WINDOW_APPROACHING));

        when(motechSchedulerService.getScheduledJobTimings(subject, jobId, fromDate, toDate)).thenReturn(new ArrayList<Date>());
        assertNull(providerReminderScheduler.getNextFireTime(ADHERENCE_WINDOW_APPROACHING));
    }
}