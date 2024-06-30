package com.vendor.management.system.user.service.application.rest;

import com.vendor.management.system.application.security.SecurityConfig;
import com.vendor.management.system.application.util.UrlHelper;
import com.vendor.management.system.domain.valueobject.Role;
import com.vendor.management.system.domain.valueobject.UserField;
import com.vendor.management.system.domain.valueobject.UserId;
import com.vendor.management.system.user.service.domain.dto.CreateUserCommand;
import com.vendor.management.system.user.service.domain.dto.DeleteUserCommand;
import com.vendor.management.system.user.service.domain.dto.UpdateUserCommand;
import com.vendor.management.system.user.service.domain.dto.UserResponse;
import com.vendor.management.system.user.service.domain.exception.UserDomainException;
import com.vendor.management.system.user.service.domain.exception.UserNotFoundException;
import com.vendor.management.system.user.service.domain.ports.input.service.UserApplicationService;
import com.vendor.management.system.user.service.domain.valueobject.Attribute;
import com.vendor.management.system.user.service.domain.valueobject.ExecutionUser;
import com.vendor.management.system.user.service.domain.valueobject.UserAttribute;
import com.vendor.management.system.user.service.domain.valueobject.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = UrlHelper.UserServiceUrl.ROOT, produces = "application/vnd.api.v1+json")
public class UserController {
    private final UserApplicationService userApplicationService;
    private final SecurityConfig securityConfig;

    public UserController(UserApplicationService userApplicationService, SecurityConfig securityConfig) {
        this.userApplicationService = userApplicationService;
        this.securityConfig = securityConfig;
    }

    private ExecutionUser generateExecutionUser(Authentication authentication) {
        return new ExecutionUser(new UserId(securityConfig.getUserId(authentication).orElse(null)), securityConfig.getAppRoles(authentication));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> fetchUsers(Authentication authentication,
                                                         @RequestParam int pageNumber,
                                                         @RequestParam int pageSize) {
        ExecutionUser executionUser = generateExecutionUser(authentication);
        UserAttribute userAttribute = new UserAttribute(Map.of(Attribute.VENDOR_ID, executionUser.getUserId().getValue()));

        log.info("Fetching users for vendor with id: {} and pageNumber: {}, pageSize: {}",
                executionUser.getUserId().getValue(), pageNumber, pageSize);
        List<UserResponse> users = userApplicationService.fetchUsers(userAttribute, pageNumber, pageSize);
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }
        log.info("Fetched: {} users for vendor with id: {} and pageNumber: {}, pageSize: {}",
                users.size(), executionUser.getUserId().getValue(), pageNumber, pageSize);
        return ResponseEntity.ok(users);
    }

    @GetMapping(UrlHelper.UserServiceUrl.CUSTOMERS_LIST)
    public ResponseEntity<List<UserResponse>> fetchCustomers(Authentication authentication,
                                                             @RequestParam UserField userField,
                                                             @RequestParam String value,
                                                             @RequestParam int pageNumber,
                                                             @RequestParam int pageSize) {
        log.info("Fetching customers by: {} with value: {} and pageNumber: {}, pageSize: {}", userField, value, pageNumber, pageSize);
        List<UserResponse> users = userApplicationService.fetchUsers(userField, value, pageNumber, pageSize);
        if (users == null || users.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }
        log.info("Fetched: {} customers by: {} with value: {} and pageNumber: {}, pageSize: {}", users.size(), userField, value, pageNumber, pageSize);
        return ResponseEntity.ok(users);
    }

    @PostMapping(UrlHelper.UserServiceUrl.CREATE_CUSTOMER)
    public ResponseEntity<Void> createCustomer(@RequestBody CreateUserCommand createUserCommand) {
        createUser(List.of(Role.CUSTOMER), createUserCommand);
        return ResponseEntity.ok().build();
    }

    @PostMapping(UrlHelper.UserServiceUrl.CREATE_VENDOR)
    public ResponseEntity<Void> createVendor(@RequestBody CreateUserCommand createUserCommand) {
        createUser(List.of(Role.VENDOR), createUserCommand);
        return ResponseEntity.ok().build();
    }

    @PostMapping(UrlHelper.UserServiceUrl.CREATE_VENDOR_USER)
    public ResponseEntity<Void> createVendorUser(Authentication authentication, @RequestBody CreateUserCommand createUserCommand) {
        createUser(authentication, List.of(Role.VENDOR_USER), createUserCommand);
        return ResponseEntity.ok().build();
    }

    private UserResponse createUser(List<Role> roles, CreateUserCommand createUserCommand) {
        return createUser(null, roles, createUserCommand);
    }

    private UserResponse createUser(Authentication authentication, List<Role> roles, CreateUserCommand createUserCommand) {
        log.info("Fetching user roles");
        List<UserRole> userRolesList = userApplicationService.fetchUserRoles();

        log.info("Matching user roles to assignable roles");
        Map<Role, Role> rolesMap = roles.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
        List<UserRole> matchedRoles = userRolesList.stream()
                .filter(userRole -> rolesMap.containsKey(userRole.getRole()))
                .toList();

        if (matchedRoles.isEmpty()) throw new UserDomainException("No matching roles found!");

        log.info("Creating user account with username: {} & email: {}", createUserCommand.getUsername(), createUserCommand.getEmail());
        ExecutionUser executionUser;
        UserAttribute attributes = null;
        if (authentication == null) {
            executionUser = new ExecutionUser(new UserId(Role.GUEST.getValue()), List.of(Role.GUEST));
        } else {
            executionUser = generateExecutionUser(authentication);
            attributes = new UserAttribute(Map.of(Attribute.VENDOR_ID, executionUser.getUserId().getValue()));
        }
        UserResponse userResponse = userApplicationService.createUser(executionUser, createUserCommand, matchedRoles, attributes, authentication != null);
        log.info("Successfully created user with id: {}", userResponse.getUserId());
        return userResponse;
    }

    @PatchMapping
    public ResponseEntity<List<Void>> updateUser(Authentication authentication, @RequestBody UpdateUserCommand updateUserCommand) {
        log.info("Updating user with id: {}, payload: {}", updateUserCommand.getUserId(), updateUserCommand);
        UserResponse userResponse = userApplicationService.updateUser(generateExecutionUser(authentication), updateUserCommand);

        log.info("Successfully updated user with id: {}", userResponse.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<List<Void>> deleteUser(Authentication authentication, @RequestBody DeleteUserCommand deleteUserCommand) {
        log.info("Deleting user with id: {}, payload: {}", deleteUserCommand.getUserId(), deleteUserCommand);
        UserResponse userResponse = userApplicationService.deleteUser(generateExecutionUser(authentication), deleteUserCommand);

        log.info("Successfully deleted user with id: {}", userResponse.getUserId());
        return ResponseEntity.ok().build();
    }

}
