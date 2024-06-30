package com.vendor.management.system.user.service.domain;

import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.event.UserCreatedEvent;
import com.vendor.management.system.user.service.domain.event.UserDeletedEvent;
import com.vendor.management.system.user.service.domain.event.UserUpdatedEvent;
import com.vendor.management.system.user.service.domain.valueobject.ExecutionUser;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.vendor.management.system.domain.util.DomainConstants.UTC;

@Slf4j
public class UserDomainServiceImpl implements UserDomainService{
    @Override
    public UserCreatedEvent create(ExecutionUser executionUser, User user) {
        user.createUser(executionUser);
        log.info("User with username: {} is creatable",user.getUsername().getValue());
        return new UserCreatedEvent(user, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public UserUpdatedEvent update(ExecutionUser executionUser, User user) {
        user.updateUser(executionUser);
        log.info("User with id: {} is updatable",user.getId().getValue());
        return new UserUpdatedEvent(user, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public UserDeletedEvent delete(ExecutionUser executionUser, User user) {
        user.deleteUser(executionUser);
        log.info("User with id: {} is deletable",user.getId().getValue());
        return new UserDeletedEvent(user, ZonedDateTime.now(ZoneId.of(UTC)));
    }
}
