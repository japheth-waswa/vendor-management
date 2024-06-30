package com.vendor.management.system.user.service.dataaccess.adapter;

import com.vendor.management.system.domain.util.Helpers;
import com.vendor.management.system.domain.valueobject.UserField;
import com.vendor.management.system.domain.valueobject.UserId;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.Role;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.UserRepresentationManager;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.UserRepresentationResponse;
import com.vendor.management.system.user.service.dataaccess.keycloak.mapper.KeycloakDataMapper;
import com.vendor.management.system.user.service.dataaccess.keycloak.service.KeycloakAdmin;
import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.exception.UserDataAccessException;
import com.vendor.management.system.user.service.domain.ports.output.repository.UserRepository;
import com.vendor.management.system.user.service.domain.valueobject.UserAttribute;
import com.vendor.management.system.user.service.domain.valueobject.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {
    private final KeycloakDataMapper keycloakDataMapper;
    private final KeycloakAdmin keycloakAdmin;

    public UserRepositoryImpl(KeycloakDataMapper keycloakDataMapper, KeycloakAdmin keycloakAdmin) {
        this.keycloakDataMapper = keycloakDataMapper;
        this.keycloakAdmin = keycloakAdmin;
    }

    @Override
    public User save(User user) {
        log.info("Parsing user roles to match keycloak roles during creation");
        List<Role> roles = keycloakDataMapper.transformUserRolesToKeycloakRoles(user, getRoles());
        if (roles.isEmpty()) {
            throw new UserDataAccessException("User does not have valid role(s)");
        }
        UserRepresentationManager userRepresentationManager = keycloakDataMapper.transformUserToCreateUserRepresentationManager(user);
        log.info("Creating user: {}", userRepresentationManager);
        keycloakAdmin.createUser(userRepresentationManager).block();

        log.info("Fetching user by username: {}", userRepresentationManager.getUsername());
        UserRepresentationResponse userRepresentationResponse = keycloakAdmin.fetchUserByUsername(userRepresentationManager.getUsername()).block();
        if (userRepresentationResponse == null) {
            throw new UserDataAccessException("User with username: " + userRepresentationManager.getUsername() + " not found!");
        }
        log.info("Assigning user with username: {} & id: {} the following roles: {}",
                userRepresentationManager.getUsername(), userRepresentationResponse.getId(), roles);
        keycloakAdmin.assignRolesToUser(userRepresentationResponse.getId(), roles).block();

        log.info("Fetching roles for user with username: {} & id: {}",
                userRepresentationManager.getUsername(), userRepresentationResponse.getId());
        List<Role> userRoles = keycloakAdmin.fetchUserRoles(userRepresentationResponse.getId()).block();

        return keycloakDataMapper.transformUserRepresentationResponseToUser(userRepresentationResponse, userRoles);
    }

    @Override
    public User update(User user) {

        UserRepresentationManager userRepresentationManager = keycloakDataMapper.transformUserToUpdateUserRepresentationManager(user);
        log.info("Updating user: {}", userRepresentationManager);
        keycloakAdmin.updateUser(user.getId().getValue(), userRepresentationManager).block();

        log.info("Fetching user by id: {}", user.getId().getValue());
        UserRepresentationResponse userRepresentationResponse = keycloakAdmin.fetchUserByUserId(user.getId().getValue()).block();
        if (userRepresentationResponse == null) {
            throw new UserDataAccessException("User with id: " + user.getId().getValue() + " not found!");
        }

        log.info("Fetching roles for user with id: {}", userRepresentationResponse.getId());
        List<Role> userRoles = keycloakAdmin.fetchUserRoles(userRepresentationResponse.getId()).block();

        return keycloakDataMapper.transformUserRepresentationResponseToUser(userRepresentationResponse, userRoles);
    }

    private List<Role> getRoles() {
        List<Role> roles = keycloakAdmin.fetchRoles().block();
        keycloakDataMapper.mapApplicationRoles(roles);
        if (roles == null || roles.isEmpty()) {
            throw new UserDataAccessException("Valid roles missing!");
        }
        return roles;
    }

    @Override
    public void delete(User user) {
        log.info("Deleting user with id: {}", user.getId().getValue());
        keycloakAdmin.deleteUser(user.getId().getValue()).block();
    }

    @Override
    public Optional<User> findById(UserId userId) {
        log.info("Fetching specific user with id: {}", userId.getValue());
        UserRepresentationResponse userRepresentationResponse = keycloakAdmin.fetchUserByUserId(userId.getValue()).block();
        if (userRepresentationResponse == null) {
            return Optional.empty();
        }
        log.info("Fetching roles for specific user with id: {}", userId.getValue());
        List<Role> userRoles = keycloakAdmin.fetchUserRoles(userId.getValue()).block();
        return Optional.of(keycloakDataMapper.transformUserRepresentationResponseToUser(userRepresentationResponse, userRoles));
    }

    @Override
    public Optional<List<UserRole>> findAllRoles() {
        List<Role> roles;
        try {
            log.info("Fetching keycloak roles");
            roles = keycloakAdmin.fetchRoles().block();
        } catch (UserDataAccessException e) {
            return Optional.empty();
        }
        log.info("Parsing keycloak roles to user roles");
        List<UserRole> userRoles = keycloakDataMapper.transformKeycloakRolesToUserRoles(roles);
        if (userRoles.isEmpty()) return Optional.empty();
        return Optional.of(userRoles);
    }

    @Override
    public Optional<List<User>> findAllByAttributes(UserAttribute attributes, int pageNumber, int pageSize) {
        return findUsers(attributes, null, pageNumber, pageSize);
    }

    @Override
    public Optional<List<User>> findAllByUserField(UserField userField, String value, int pageNumber, int pageSize) {
        return findUsers(null, Map.of(getUserField(userField), value), pageNumber, pageSize);
    }

    private String getUserField(UserField userField) {
        return switch (userField) {
            case FIRST_NAME -> UserField.FIRST_NAME.getValue();
            case LAST_NAME -> UserField.LAST_NAME.getValue();
            case EMAIL -> UserField.EMAIL.getValue();
        };
    }

    private Optional<List<User>> findUsers(UserAttribute attributes, Map<String, String> userFieldAttributes, int pageNumber, int pageSize) {
        List<UserRepresentationResponse> users = keycloakAdmin
                .fetchUsers(Helpers.parsePagination(pageNumber, pageSize),
                        attributes != null ? attributes.getValue() : null,
                        userFieldAttributes).block();
        if (users == null || users.isEmpty()) return Optional.empty();
        return Optional.of(users.stream()
                .map(userRepresentationResponse -> {
                    List<Role> userRoles = Collections.emptyList();
                    try {
                        userRoles = keycloakAdmin.fetchUserRoles(userRepresentationResponse.getId()).block();
                    } catch (UserDataAccessException e) {
                    }
                    return keycloakDataMapper.transformUserRepresentationResponseToUser(userRepresentationResponse, userRoles);
                })
                .collect(Collectors.toList()));
    }

}
