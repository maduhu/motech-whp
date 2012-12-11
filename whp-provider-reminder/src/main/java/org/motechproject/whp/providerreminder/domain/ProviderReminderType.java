package org.motechproject.whp.providerreminder.domain;

import static org.motechproject.whp.common.event.EventKeys.ADHERENCE_WINDOW_APPROACHING_EVENT_NAME;

public enum ProviderReminderType {

    ADHERENCE_WINDOW_APPROACHING(ADHERENCE_WINDOW_APPROACHING_EVENT_NAME);

    private String eventSubject;

    ProviderReminderType(String eventSubject) {

        this.eventSubject = eventSubject;
    }

    public String getEventSubject() {
        return eventSubject;
    }
}
