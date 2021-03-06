package org.motechproject.whp.schedule.service;

import org.motechproject.event.EventRelay;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.whp.common.event.EventKeys;
import org.motechproject.whp.schedule.domain.ScheduleTimings;
import org.motechproject.whp.schedule.domain.ScheduleType;
import org.motechproject.whp.schedule.model.ScheduleConfiguration;
import org.motechproject.whp.schedule.repository.AllScheduleConfigurations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.motechproject.scheduler.MotechSchedulerService.JOB_ID_KEY;
import static org.motechproject.util.DateUtil.now;

@Component
public class WHPSchedulerService {

    private MotechSchedulerService motechSchedulerService;
    private AllScheduleConfigurations allScheduleConfigurations;
    private EventRelay eventRelay;

    @Autowired
    public WHPSchedulerService(MotechSchedulerService motechSchedulerService, AllScheduleConfigurations allScheduleConfigurations, EventRelay eventRelay) {
        this.motechSchedulerService = motechSchedulerService;
        this.allScheduleConfigurations = allScheduleConfigurations;
        this.eventRelay = eventRelay;
    }

    public void scheduleEvent(ScheduleConfiguration configuration) {
        schedule(configuration);
        configuration.setScheduled(true);
        allScheduleConfigurations.saveOrUpdate(configuration);
    }

    public ScheduleConfiguration getReminder(ScheduleType jobType) {
        List<Date> scheduledJobTimings = getScheduleJobTimings(jobType, new ScheduleTimings(now()));
        if (scheduledJobTimings.isEmpty()) {
            return null;
        } else {
            ScheduleConfiguration configuration = allScheduleConfigurations.withType(jobType);
            configuration.updateDateTimeValues(scheduledJobTimings.get(0));
            return configuration;
        }
    }

    public ScheduleConfiguration configuration(ScheduleType reminderType) {
        return allScheduleConfigurations.withType(reminderType);
    }

    public void unScheduleReminder(ScheduleConfiguration configuration) {
        configuration.setScheduled(false);
        motechSchedulerService.unscheduleJob(configuration.getScheduleType().getEventSubject(), configuration.getScheduleType().name());
        allScheduleConfigurations.saveOrUpdate(configuration);
    }

    private void schedule(ScheduleConfiguration reminderConfiguration) {
        ScheduleType scheduleType = reminderConfiguration.getScheduleType();
        MotechEvent motechEvent = createMotechEvent(scheduleType, reminderConfiguration.getMessageId());
        motechSchedulerService.scheduleJob(new CronSchedulableJob(motechEvent, reminderConfiguration.generateCronExpression()));
    }

    private MotechEvent createMotechEvent(ScheduleType reminderType, String messageId) {
        MotechEvent motechEvent = new MotechEvent(reminderType.getEventSubject());
        motechEvent.getParameters().put(JOB_ID_KEY, reminderType.name());
        motechEvent.getParameters().put(EventKeys.SCHEDULE_CONFIGURATION_MESSAGE_ID, messageId);
        return motechEvent;
    }

    private List<Date> getScheduleJobTimings(ScheduleType jobType, ScheduleTimings timings) {
        try {
            return motechSchedulerService.getScheduledJobTimings(jobType.getEventSubject(), jobType.name(), timings.getStartDate(), timings.getEndDate());
        } catch (Exception ignored) {
            return emptyList();
        }
    }

    public void execute(ScheduleType scheduleType, String messageId) {
        MotechEvent motechEvent = createMotechEvent(scheduleType, messageId);
        eventRelay.sendEventMessage(motechEvent);
    }
}
