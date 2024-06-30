package com.vendor.management.system.user.service.domain.event;

import com.vendor.management.system.user.service.domain.entity.User;

import java.time.ZonedDateTime;

public class UserCreatedEvent extends UserEvent{
    public UserCreatedEvent(User user, ZonedDateTime createdAt) {
        super(user, createdAt);
    }
}
