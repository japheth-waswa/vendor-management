package com.vendor.management.system.user.service.dataaccess.keycloak.mapper;

import com.vendor.management.system.domain.valueobject.Names;
import com.vendor.management.system.domain.valueobject.UserId;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.Role;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.UserRepresentationManager;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.UserRepresentationResponse;
import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class KeycloakDataMapper {

    public void mapApplicationRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) return;
        Map<String, String> appRoles = new HashMap<>();
        Stream.of(com.vendor.management.system.domain.valueobject.Role.values())
                .forEach(role -> appRoles.put(role.getValue(), role.name()));
        roles.removeIf(role -> !appRoles.containsKey(role.getName()));
    }

    public UserRepresentationManager transformUserToCreateUserRepresentationManager(User user) {
        return transformUserToUserRepresentationManager(user, false);
    }

    public UserRepresentationManager transformUserToUpdateUserRepresentationManager(User user) {
        return transformUserToUserRepresentationManager(user, true);
    }

    private UserRepresentationManager transformUserToUserRepresentationManager(User user, boolean isUpdate) {
        return UserRepresentationManager.builder()
                .username(isUpdate ? null : user.getUsername().getValue())
                .email(user.getEmail().getValue())
                .enabled(user.getEnabled() == null ? null : user.getEnabled())
                .emailVerified(user.getEmailVerified() == null ? null : user.getEmailVerified())
                .firstName(user.getNames() == null
                        || user.getNames().getFirstName() == null
                        || user.getNames().getFirstName().isBlank() ? null : user.getNames().getFirstName())
                .lastName(user.getNames() == null
                        || user.getNames().getLastName() == null
                        || user.getNames().getLastName().isBlank() ? null : user.getNames().getLastName())
                .credentials(isUpdate ? null : (user.getCredentials() == null || user.getCredentials().isEmpty() ? null :
                        user.getCredentials().stream()
                                .map(userCredential ->
                                        UserRepresentationManager.Credential.builder()
                                                .type(userCredential.getCredentialType().getValue())
                                                .value(userCredential.getValue())
                                                .temporary(userCredential.isTemporary())
                                                .build()
                                )
                                .toList()))
                .attributes(isUpdate ? null :
                        (user.getAttributes() == null || user.getAttributes().getValue() == null
                                || user.getAttributes().getValue().isEmpty() ? null :
                                user.getAttributes().getValue().entrySet().stream()
                                        .filter(entry -> entry.getKey() != null && !entry.getValue().isBlank())
                                        .collect(HashMap::new, (map, entry) -> map.put(entry.getKey().getValue(), entry.getValue()), HashMap::putAll)))
                .build();
    }

    public List<Role> transformUserRolesToKeycloakRoles(User user, List<Role> roles) {
        if (roles == null || roles.isEmpty()
                || user.getRoles() == null || user.getRoles().isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, String> appRoles = roles.stream()
                .collect(Collectors.toMap(Role::getName, Role::getId));

        return user.getRoles().stream()
                .map(userRole -> userRole.getRole().getValue())
                .filter(appRoles::containsKey)
                .map(roleValue -> Role.builder()
                        .id(appRoles.get(roleValue))
                        .name(roleValue)
                        .build())
                .collect(Collectors.toList());
    }

    public User transformUserRepresentationResponseToUser(UserRepresentationResponse userRepresentationResponse, List<Role> roles) {
        Map<Attribute, String> userAttributes = userRepresentationResponse.getAttributes() == null
                || userRepresentationResponse.getAttributes().isEmpty()
                ? Collections.emptyMap() : userRepresentationResponse.getAttributes().entrySet().stream()
                .filter(entry -> entry.getKey() != null && Attribute.containsValue(entry.getKey()) && !entry.getValue().isEmpty())
                .collect(HashMap::new,
                        (map, entry) -> map.put(Attribute.extractAttribute(entry.getKey()), entry.getValue().get(0)),
                        HashMap::putAll);
        return User.builder()
                .userId(new UserId(userRepresentationResponse.getId()))
                .username(new Username(userRepresentationResponse.getUsername()))
                .email(new Email(userRepresentationResponse.getEmail()))
                .names(new Names(userRepresentationResponse.getFirstName(), userRepresentationResponse.getLastName()))
                .enabled(userRepresentationResponse.isEnabled())
                .emailVerified(userRepresentationResponse.isEmailVerified())
                .attributes(userAttributes.isEmpty() ? null
                        : new UserAttribute(userAttributes))
                .roles(transformKeycloakRolesToUserRoles(roles))
                .build();
    }

    public List<UserRole> transformKeycloakRolesToUserRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(role -> com.vendor.management.system.domain.valueobject.Role.containsValue(role.getName()))
                .map(role -> new UserRole(role.getId(), com.vendor.management.system.domain.valueobject.Role.extractRole(role.getName())))
                .collect(Collectors.toList());
    }
}
