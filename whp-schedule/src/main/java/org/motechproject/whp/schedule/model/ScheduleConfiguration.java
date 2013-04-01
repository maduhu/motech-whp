package org.motechproject.whp.schedule.model;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDateTime;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.whp.schedule.domain.ScheduleType;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@TypeDiscriminator("doc.type === 'ScheduleConfiguration'")
public class ScheduleConfiguration extends MotechBaseDataObject {

    @NotNull
    private DayOfWeek dayOfWeek;
    @NotNull
    private int hour;
    @NotNull
    private int minute;
    @NotNull
    private ScheduleType scheduleType;

    private String messageId;

    private boolean scheduled = false;

    public ScheduleConfiguration() {
    }

    public ScheduleConfiguration(ScheduleType reminderType, Date date) {
        setScheduleType(reminderType);
        updateDateTimeValues(date);
    }

    public void updateDateTimeValues(Date date) {
        LocalDateTime localDateTime = new LocalDateTime(date);
        setDayOfWeek(DayOfWeek.getDayOfWeek(localDateTime.getDayOfWeek()));
        setHour(localDateTime.getHourOfDay());
        setMinute(localDateTime.getMinuteOfHour());
    }

    public void setScheduleType(ScheduleType scheduleType) {
        if (StringUtils.isBlank(getId())) {
            setId(scheduleType.name());
        }
        this.scheduleType = scheduleType;
    }

    public String generateCronExpression() {
        String weekDay = getDayOfWeek().getShortName();
        return String.format("0 %s %s ? * %s", minute, hour, weekDay);
    }
}