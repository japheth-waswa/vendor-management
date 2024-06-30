package com.vendor.management.system.user.service.domain.ports.input.service;

import com.vendor.management.system.domain.valueobject.UserField;
import com.vendor.management.system.user.service.domain.dto.CreateUserCommand;
import com.vendor.management.system.user.service.domain.dto.DeleteUserCommand;
import com.vendor.management.system.user.service.domain.dto.UpdateUserCommand;
import com.vendor.management.system.user.service.domain.dto.UserResponse;
import com.vendor.management.system.user.service.domain.valueobject.ExecutionUser;
import com.vendor.management.system.user.service.domain.valueobject.UserAttribute;
import com.vendor.management.system.user.service.domain.valueobject.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface UserApplicationService {
    List<UserRole> fetchUserRoles();

    UserResponse createUser(@NotNull ExecutionUser executionUser,
                            @Valid CreateUserCommand createUserCommand,
                            @NotNull @NotEmpty List<UserRole> roles,
                            UserAttribute attributes,
                            boolean mustResetPassword);

    UserResponse updateUser(@NotNull ExecutionUser executionUser, @Valid UpdateUserCommand updateUserCommand);

    UserResponse deleteUser(@NotNull ExecutionUser executionUser, @Valid DeleteUserCommand deleteUserCommand);

    List<UserResponse> fetchUsers(@NotNull UserAttribute attributes, @Min(0) int pageNumber, @Min(1) @Max(10) int pageSize);

    List<UserResponse> fetchUsers(@NotNull UserField userField, @NotNull @NotEmpty String value, @Min(0) @Max(10) int pageNumber, @Min(1) int pageSize);
}
