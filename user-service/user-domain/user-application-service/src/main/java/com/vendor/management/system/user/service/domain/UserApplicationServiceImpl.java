package com.vendor.management.system.user.service.domain;

import com.vendor.management.system.domain.valueobject.UserField;
import com.vendor.management.system.user.service.domain.command.UserCommandHandle;
import com.vendor.management.system.user.service.domain.dto.CreateUserCommand;
import com.vendor.management.system.user.service.domain.dto.DeleteUserCommand;
import com.vendor.management.system.user.service.domain.dto.UpdateUserCommand;
import com.vendor.management.system.user.service.domain.dto.UserResponse;
import com.vendor.management.system.user.service.domain.ports.input.service.UserApplicationService;
import com.vendor.management.system.user.service.domain.valueobject.ExecutionUser;
import com.vendor.management.system.user.service.domain.valueobject.UserAttribute;
import com.vendor.management.system.user.service.domain.valueobject.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
public class UserApplicationServiceImpl implements UserApplicationService {
    private final UserCommandHandle userCommandHandler;

    public UserApplicationServiceImpl(UserCommandHandle userCommandHandler) {
        this.userCommandHandler = userCommandHandler;
    }

    @Override
    public List<UserRole> fetchUserRoles() {
        return userCommandHandler.userRoles();
    }

    @Override
    public UserResponse createUser(ExecutionUser executionUser, CreateUserCommand createUserCommand, List<UserRole> roles, UserAttribute attributes, boolean mustResetPassword) {
        return userCommandHandler.createUser(executionUser, createUserCommand, roles, attributes, mustResetPassword);
    }

    @Override
    public UserResponse updateUser(ExecutionUser executionUser, UpdateUserCommand updateUserCommand) {
        return userCommandHandler.updateUser(executionUser, updateUserCommand);
    }

    @Override
    public UserResponse deleteUser(ExecutionUser executionUser, DeleteUserCommand deleteUserCommand) {
        return userCommandHandler.deleteUser(executionUser, deleteUserCommand);
    }

    @Override
    public List<UserResponse> fetchUsers(UserAttribute attributes, int pageNumber, int pageSize) {
        return userCommandHandler.fetchUsers(attributes, pageNumber, pageSize);
    }

    @Override
    public List<UserResponse> fetchUsers(UserField userField, String value, int pageNumber, int pageSize) {
        return userCommandHandler.fetchUsers(userField, value, pageNumber, pageSize);
    }
}
