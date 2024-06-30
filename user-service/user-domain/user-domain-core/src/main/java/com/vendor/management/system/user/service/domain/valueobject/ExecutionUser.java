package com.vendor.management.system.user.service.domain.valueobject;

import com.vendor.management.system.domain.valueobject.Role;
import com.vendor.management.system.domain.valueobject.UserId;
import com.vendor.management.system.user.service.domain.exception.UserDomainException;

import java.util.List;

public class ExecutionUser {
    private final UserId userId;
    private final List<Role> roles;

    public ExecutionUser(UserId userId, List<Role> roles) {
        if (roles.isEmpty()) {
            throw new UserDomainException("User must have at least one role");
        }
        this.userId = userId;
        this.roles = roles;
    }

    public UserId getUserId() {
        return userId;
    }

    public List<Role> getRoles() {
        return roles;
    }
}
