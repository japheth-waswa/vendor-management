package com.vendor.management.system.domain.event.payload;

import java.time.ZonedDateTime;

public abstract class EventPayload {
    public abstract ZonedDateTime getCreatedAtDate();
    public abstract String getEventName();
    public abstract String getEventId();
}
