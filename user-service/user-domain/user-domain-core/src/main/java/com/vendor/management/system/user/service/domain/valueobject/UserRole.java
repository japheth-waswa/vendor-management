package com.vendor.management.system.user.service.domain.valueobject;

import com.vendor.management.system.domain.valueobject.Role;

public class UserRole {
    private final String id;
    private final Role role;

    public UserRole(String id, Role role) {
        this.id = id;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRole userRole = (UserRole) o;
        return id.equals(userRole.id) && role == userRole.role;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + role.hashCode();
        return result;
    }
}
