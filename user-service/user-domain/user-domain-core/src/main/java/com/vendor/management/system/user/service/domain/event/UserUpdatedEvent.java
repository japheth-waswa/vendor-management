package com.vendor.management.system.user.service.domain.event;

import com.vendor.management.system.user.service.domain.entity.User;

import java.time.ZonedDateTime;

public class UserUpdatedEvent extends UserEvent{
    public UserUpdatedEvent(User user, ZonedDateTime createdAt) {
        super(user, createdAt);
    }
}
