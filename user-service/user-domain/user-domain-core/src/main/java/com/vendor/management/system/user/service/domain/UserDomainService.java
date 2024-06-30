package com.vendor.management.system.user.service.domain;

import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.event.UserCreatedEvent;
import com.vendor.management.system.user.service.domain.event.UserDeletedEvent;
import com.vendor.management.system.user.service.domain.event.UserUpdatedEvent;
import com.vendor.management.system.user.service.domain.valueobject.ExecutionUser;

public interface UserDomainService {
    UserCreatedEvent create(ExecutionUser executionUser,User user);
    UserUpdatedEvent update(ExecutionUser executionUser,User user);
    UserDeletedEvent delete(ExecutionUser executionUser,User user);
}
