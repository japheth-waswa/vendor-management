package com.vendor.management.system.user.service.domain.entity;

import com.vendor.management.system.domain.entity.AggregateRoot;
import com.vendor.management.system.domain.valueobject.Names;
import com.vendor.management.system.domain.valueobject.Role;
import com.vendor.management.system.domain.valueobject.UserId;
import com.vendor.management.system.user.service.domain.exception.UserDomainException;
import com.vendor.management.system.user.service.domain.valueobject.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User extends AggregateRoot<UserId> {
    private Username username;
    private final Email email;
    private final Names names;
    private final Boolean enabled;
    private final Boolean emailVerified;
    private List<UserCredential> credentials;
    private UserAttribute attributes;
    private List<UserRole> roles;

    public void createUser(ExecutionUser executionUser) {
        validateAccountCreation(executionUser);
    }

    private void validateAccountCreation(ExecutionUser executionUser) {
        validateUsername();
        validateEmail();
        validateNames();
        validateEnabled();
        validateEmailVerified();
        validateRoles(executionUser);
        validateCredentials();
        validateAttributes(executionUser);
    }

    private void validateUsername() {
        if (getUsername() == null || getUsername().getValue() == null || getUsername().getValue().isBlank()) {
            throw new UserDomainException("Username cannot be empty!");
        }
    }

    private void validateEmail() {
        if (getEmail() == null || getEmail().getValue() == null || getEmail().getValue().isBlank()) {
            throw new UserDomainException("Email cannot be empty!");
        }
    }

    private void validateNames() {
        if (getNames() == null
                || getNames().getFirstName() == null || getNames().getFirstName().isBlank()
                || getNames().getLastName() == null || getNames().getLastName().isBlank()) {
            throw new UserDomainException("First and last names must be provided!");
        }
    }

    private void validateEnabled() {
        if (enabled == null) {
            throw new UserDomainException("Set whether the account is enabled!");
        }
    }

    private void validateEmailVerified() {
        if (emailVerified == null) {
            throw new UserDomainException("Set whether the email is verified!");
        }
    }

    private void validateCredentials() {
        boolean isInvalidCredentials = credentials == null || credentials.isEmpty() || credentials.stream()
                .anyMatch(credential -> credential.getValue() == null || credential.getValue().isBlank());
        if (isInvalidCredentials) {
            throw new UserDomainException("Invalid credentials!");
        }
    }

    private void validateRoles(ExecutionUser executionUser) {
        List<UserRole> validRoles = roles == null || roles.isEmpty() ? Collections.emptyList() :
                roles.stream().filter(role -> role.getRole().equals(Role.VENDOR)
                                || role.getRole().equals(Role.CUSTOMER)
                                || role.getRole().equals(Role.VENDOR_USER))
                        .toList();
        if (validRoles.isEmpty()) {
            throw new UserDomainException("Invalid role(s)");
        }
        UserRole role = validRoles.get(0);
        if (attributes != null
                && (role.getRole().equals(Role.VENDOR) || role.getRole().equals(Role.CUSTOMER))) {
            //clear the attributes
            attributes.setAttributes(new HashMap<>());
        }
        roles = List.of(role);
    }

    private void validateAttributes(ExecutionUser executionUser) {
        if (roles.isEmpty() || roles.stream().noneMatch(role -> role.getRole().equals(Role.VENDOR_USER))) {
            return;
        }
        validateExecutionUserRole(executionUser, Role.VENDOR, "Executing role must be " + Role.VENDOR.getValue());
        if (attributes == null || attributes.getValue() == null || attributes.getValue().isEmpty()) {
            attributes = new UserAttribute(Map.of(Attribute.VENDOR_ID, executionUser.getUserId().getValue()));
        } else {
            //change the attributes map to mutable,just in-case the provided is immutable.
            attributes.setAttributes(new HashMap<>(attributes.getValue()));
            attributes.getValue().put(Attribute.VENDOR_ID, executionUser.getUserId().getValue());
        }
    }


    public void updateUser(ExecutionUser executionUser) {
        if (executionUser.getUserId().equals(getId())) {
            //only update if it belongs to the current user
            resetSomeFields();
            return;
        }
        //only vendor allowed to update a vendor-user
        validateUserUpdateAndDeletion(executionUser, true);
    }

    public void deleteUser(ExecutionUser executionUser) {
        //only vendor allowed to delete a vendor-user
        validateUserUpdateAndDeletion(executionUser, false);
    }

    private void validateUserUpdateAndDeletion(ExecutionUser executionUser, boolean isUpdate) {
        validateExecutionUserRole(executionUser, Role.VENDOR, "Executing role not allowed to " + (isUpdate ? "update" : "delete") + " this user!");

        if (getId() == null || getId().getValue() == null || getId().getValue().isBlank()) {
            throw new UserDomainException("User id cannot be null for " + (isUpdate ? "update" : "delete") + " action");
        }

        if (roles == null || roles.isEmpty()) {
            throw new UserDomainException("User must have at-least one role");
        }
        boolean userHasValidRoleForAction = roles.stream()
                .anyMatch(userRole -> userRole.getRole().equals(Role.VENDOR_USER));

        if (!userHasValidRoleForAction) {
            throw new UserDomainException("User does not have valid role for " + (isUpdate ? "update!" : "delete!"));
        }
        if (attributes == null || attributes.getValue() == null || attributes.getValue().isEmpty() || !attributes.getValue().containsKey(Attribute.VENDOR_ID)) {
            throw new UserDomainException("User missing relevant attributes for " + (isUpdate ? "update!" : "delete!") + " action");
        }

        if (!attributes.getValue().get(Attribute.VENDOR_ID).equals(executionUser.getUserId().getValue())) {
            throw new UserDomainException("Resource does not belong to this user!");
        }
        resetSomeFields();
    }

    private void validateExecutionUserRole(ExecutionUser executionUser, Role requiredRole, String errorMessage) {
        boolean hasValidRole = executionUser.getRoles().stream()
                .anyMatch(role -> role.equals(requiredRole));
        if (!hasValidRole) {
            throw new UserDomainException(errorMessage);
        }
    }

    private void resetSomeFields() {
        username = null;
        credentials = Collections.emptyList();
        attributes = null;
        roles = Collections.emptyList();
    }

    public Username getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public Names getNames() {
        return names;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public List<UserCredential> getCredentials() {
        return credentials;
    }

    public UserAttribute getAttributes() {
        return attributes;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    private User(Builder builder) {
        super.setId(builder.userId);
        username = builder.username;
        email = builder.email;
        names = builder.names;
        enabled = builder.enabled;
        emailVerified = builder.emailVerified;
        credentials = builder.credentials;
        attributes = builder.attributes;
        roles = builder.roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UserId userId;
        private Username username;
        private Email email;
        private Names names;
        private Boolean enabled;
        private Boolean emailVerified;
        private List<UserCredential> credentials;
        private UserAttribute attributes;
        private List<UserRole> roles;

        private Builder() {
        }

        public Builder userId(UserId val) {
            userId = val;
            return this;
        }

        public Builder username(Username val) {
            username = val;
            return this;
        }

        public Builder email(Email val) {
            email = val;
            return this;
        }

        public Builder names(Names val) {
            names = val;
            return this;
        }

        public Builder enabled(Boolean val) {
            enabled = val;
            return this;
        }

        public Builder emailVerified(Boolean val) {
            emailVerified = val;
            return this;
        }

        public Builder credentials(List<UserCredential> val) {
            credentials = val;
            return this;
        }

        public Builder attributes(UserAttribute val) {
            attributes = val;
            return this;
        }

        public Builder roles(List<UserRole> val) {
            roles = val;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
