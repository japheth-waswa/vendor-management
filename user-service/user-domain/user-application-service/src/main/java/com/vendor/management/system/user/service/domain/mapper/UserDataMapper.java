package com.vendor.management.system.user.service.domain.mapper;

import com.vendor.management.system.domain.valueobject.Names;
import com.vendor.management.system.user.service.domain.dto.CreateUserCommand;
import com.vendor.management.system.user.service.domain.dto.UpdateUserCommand;
import com.vendor.management.system.user.service.domain.dto.UserResponse;
import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDataMapper {
    public User transformCreateUserCommandToUser(CreateUserCommand createUserCommand,
                                                 List<UserRole> roles,
                                                 UserAttribute attributes,
                                                 boolean mustResetPassword) {
        return User.builder()
                .username(new Username(createUserCommand.getUsername()))
                .email(new Email(createUserCommand.getEmail()))
                .names(new Names(createUserCommand.getFirstName(), createUserCommand.getLastName()))
                .enabled(createUserCommand.getEnabled())
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD,
                        createUserCommand.getPassword(),
                        mustResetPassword)))
                .attributes(attributes)
                .roles(roles)
                .emailVerified(false)
                .build();
    }

    public User transformUpdateUserCommandToUser(UpdateUserCommand updateUserCommand, User existingUser) {
        return User.builder()
                .userId(existingUser.getId())
                .username(existingUser.getUsername())
                .email(new Email(updateUserCommand.getEmail()))
                .names(new Names(updateUserCommand.getFirstName(), updateUserCommand.getLastName()))
                .enabled(updateUserCommand.getEnabled())
                .credentials(existingUser.getCredentials())
                .attributes(existingUser.getAttributes())
                .roles(existingUser.getRoles())
                .emailVerified(existingUser.getEmailVerified())
                .build();
    }

    public UserResponse transformUserToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId().getValue())
                .email(user.getEmail().getValue())
                .firstName(user.getNames().getFirstName())
                .lastName(user.getNames().getLastName())
                .enabled(user.getEnabled())
                .build();
    }
}
