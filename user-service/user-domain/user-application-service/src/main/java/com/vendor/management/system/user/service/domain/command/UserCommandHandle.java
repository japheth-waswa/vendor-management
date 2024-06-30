package com.vendor.management.system.user.service.domain.command;

import com.vendor.management.system.domain.valueobject.UserField;
import com.vendor.management.system.domain.valueobject.UserId;
import com.vendor.management.system.user.service.domain.UserDomainService;
import com.vendor.management.system.user.service.domain.dto.CreateUserCommand;
import com.vendor.management.system.user.service.domain.dto.DeleteUserCommand;
import com.vendor.management.system.user.service.domain.dto.UpdateUserCommand;
import com.vendor.management.system.user.service.domain.dto.UserResponse;
import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.exception.UserDomainException;
import com.vendor.management.system.user.service.domain.exception.UserNotFoundException;
import com.vendor.management.system.user.service.domain.mapper.UserDataMapper;
import com.vendor.management.system.user.service.domain.ports.output.repository.UserRepository;
import com.vendor.management.system.user.service.domain.valueobject.ExecutionUser;
import com.vendor.management.system.user.service.domain.valueobject.UserAttribute;
import com.vendor.management.system.user.service.domain.valueobject.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCommandHandle {
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final UserDataMapper userDataMapper;

    public UserCommandHandle(UserDomainService userDomainService,
                             UserRepository userRepository,
                             UserDataMapper userDataMapper) {
        this.userDomainService = userDomainService;
        this.userRepository = userRepository;
        this.userDataMapper = userDataMapper;
    }

    public List<UserRole> userRoles() {
        return userRepository.findAllRoles()
                .orElseThrow(() -> new UserDomainException("User roles not found!"));
    }

    public UserResponse createUser(ExecutionUser executionUser,
                                   CreateUserCommand createUserCommand,
                                   List<UserRole> roles,
                                   UserAttribute attributes,
                                   boolean mustResetPassword) {
        User user = userDataMapper.transformCreateUserCommandToUser(createUserCommand, roles, attributes, mustResetPassword);
        userDomainService.create(executionUser, user);
        return userDataMapper.transformUserToUserResponse(userRepository.save(user));
    }

    public UserResponse updateUser(ExecutionUser executionUser,
                                   UpdateUserCommand updateUserCommand) {
        User existingUser = userRepository.findById(new UserId(updateUserCommand.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        User user = userDataMapper.transformUpdateUserCommandToUser(updateUserCommand, existingUser);
        userDomainService.update(executionUser, user);
        return userDataMapper.transformUserToUserResponse(userRepository.update(user));
    }

    public UserResponse deleteUser(ExecutionUser executionUser,
                                   DeleteUserCommand deleteUserCommand) {
        User existingUser = userRepository.findById(new UserId(deleteUserCommand.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        userDomainService.delete(executionUser, existingUser);
        userRepository.delete(existingUser);
        return userDataMapper.transformUserToUserResponse(existingUser);
    }

    public List<UserResponse> fetchUsers(UserAttribute attributes, int pageNumber, int pageSize) {
        return userRepository.findAllByAttributes(attributes, pageNumber, pageSize)
                .orElseThrow(() -> new UserNotFoundException("Users not found!"))
                .stream()
                .map(userDataMapper::transformUserToUserResponse)
                .toList();
    }
    public List<UserResponse> fetchUsers(UserField userField, String value, int pageNumber, int pageSize) {
        return userRepository.findAllByUserField(userField,value, pageNumber, pageSize)
                .orElseThrow(() -> new UserNotFoundException("Users not found!"))
                .stream()
                .map(userDataMapper::transformUserToUserResponse)
                .toList();
    }
}
