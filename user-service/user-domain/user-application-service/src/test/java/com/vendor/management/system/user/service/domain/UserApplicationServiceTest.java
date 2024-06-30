package com.vendor.management.system.user.service.domain;

import com.vendor.management.system.domain.valueobject.Names;
import com.vendor.management.system.domain.valueobject.Role;
import com.vendor.management.system.domain.valueobject.UserField;
import com.vendor.management.system.domain.valueobject.UserId;
import com.vendor.management.system.user.service.domain.dto.CreateUserCommand;
import com.vendor.management.system.user.service.domain.dto.DeleteUserCommand;
import com.vendor.management.system.user.service.domain.dto.UpdateUserCommand;
import com.vendor.management.system.user.service.domain.dto.UserResponse;
import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.exception.UserDomainException;
import com.vendor.management.system.user.service.domain.exception.UserNotFoundException;
import com.vendor.management.system.user.service.domain.ports.input.service.UserApplicationService;
import com.vendor.management.system.user.service.domain.ports.output.repository.UserRepository;
import com.vendor.management.system.user.service.domain.valueobject.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = UserTestConfiguration.class)
public class UserApplicationServiceTest {

    @Autowired
    private UserApplicationService userApplicationService;
    @Autowired
    UserRepository userRepository;

    ExecutionUser validExecutionUser;
    List<UserRole> validRoles;
    UserAttribute validAttributes;
    UserAttribute invalidAttributes;
    CreateUserCommand createUserCommandCustomer;
    CreateUserCommand createUserCommandVendor;
    CreateUserCommand createUserCommandVendorUser;
    CreateUserCommand createUserCommandWrongUsername;
    UpdateUserCommand updateUserCommandValid;
    UpdateUserCommand updateUserCommandWrongUserId;
    DeleteUserCommand deleteUserCommandValid;
    DeleteUserCommand deleteUserCommandInValid;
    DeleteUserCommand deleteUserCommandWrongUserRole;

    private final String USER_ID_1 = "6ae1e36a-d698-4032-bb2c-db0fc99c33ec";
    private final String USER_ID_2 = "8536e131-787c-4257-af81-8f10bf3b5045";
    private final String USER_ID_3 = "7476e129-787c-4257-af81-8f10bf3b5046";
    private final String USER_ID_4 = "5346e129-787c-4257-af81-8f10bf3b5041";
    private final String USER_ID_5 = "1566e131-787c-4257-af81-8f10bf3b54089";
    private final String USER_ID_1_USERNAME = "user_id_1_username";
    private final String USER_ID_2_USERNAME = "user_id_2_username";
    private final String USER_ID_1_EMAIL = "user_id_1_email@example.org";
    private final String USER_ID_2_EMAIL = "user_id_2_email@example.org";
    private final String USER_ID_1_FIRSTNAME = "user_id_1_firstname";
    private final String USER_ID_2_FIRSTNAME = "user_id_2_firstname";
    private final String USER_ID_1_LASTNAME = "user_id_1_lastname";
    private final String USER_ID_2_LASTNAME = "user_id_2_lastname";
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 10;
    private final String VENDOR_1 = "vendor_id_1";
    private final String VENDOR_2 = "vendor_id_2";
    private final String ROLE_ID_1 = "role_id_1";
    private final String ROLE_ID_2 = "role_id_2";

    @BeforeAll
    public void init() {
        validExecutionUser = new ExecutionUser(new UserId(USER_ID_1), List.of(Role.VENDOR));
        validRoles = List.of(new UserRole(ROLE_ID_1, Role.VENDOR_USER));
        validAttributes = new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_1));
        invalidAttributes = new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_2));

        createUserCommandCustomer = CreateUserCommand.builder()
                .username(USER_ID_1_USERNAME)
                .email(USER_ID_1_EMAIL)
                .firstName(USER_ID_1_FIRSTNAME)
                .lastName(USER_ID_1_LASTNAME)
                .enabled(true)
                .password("1234")
                .build();
        createUserCommandVendor = CreateUserCommand.builder()
                .username(USER_ID_1_USERNAME)
                .email(USER_ID_1_EMAIL)
                .firstName(USER_ID_1_FIRSTNAME)
                .lastName(USER_ID_1_LASTNAME)
                .enabled(true)
                .password("1234")
                .build();

        createUserCommandVendorUser = CreateUserCommand.builder()
                .username(USER_ID_1_USERNAME)
                .email(USER_ID_1_EMAIL)
                .firstName(USER_ID_1_FIRSTNAME)
                .lastName(USER_ID_1_LASTNAME)
                .enabled(true)
                .password("1234")
                .build();
        createUserCommandWrongUsername = CreateUserCommand.builder()
                .username("us")
                .email(USER_ID_1_EMAIL)
                .firstName(USER_ID_1_FIRSTNAME)
                .lastName(USER_ID_1_LASTNAME)
                .enabled(true)
                .password("1234")
                .build();

        updateUserCommandValid = UpdateUserCommand.builder()
                .userId(USER_ID_1)
                .email(USER_ID_1_EMAIL)
                .firstName(USER_ID_1_FIRSTNAME)
                .lastName(USER_ID_1_LASTNAME)
                .enabled(false)
                .build();
        updateUserCommandWrongUserId = UpdateUserCommand.builder()
                .userId(USER_ID_2)
                .email(USER_ID_1_EMAIL)
                .firstName(USER_ID_1_FIRSTNAME)
                .lastName(USER_ID_1_LASTNAME)
                .enabled(false)
                .build();

        deleteUserCommandValid = DeleteUserCommand.builder()
                .userId(USER_ID_1)
                .build();
        deleteUserCommandInValid = DeleteUserCommand.builder()
                .userId(USER_ID_2)
                .build();
        deleteUserCommandWrongUserRole = DeleteUserCommand.builder()
                .userId(USER_ID_3)
                .build();

        User user_1 = User.builder()
                .userId(new UserId(USER_ID_1))
                .username(new Username(USER_ID_1_USERNAME))
                .email(new Email(USER_ID_1_EMAIL))
                .names(new Names(USER_ID_1_FIRSTNAME, USER_ID_1_LASTNAME))
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD, "1234", false)))
                .attributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_1)))
                .roles(List.of(new UserRole(ROLE_ID_1, Role.VENDOR_USER)))
                .build();
        User user_2 = User.builder()
                .userId(new UserId(USER_ID_2))
                .username(new Username(USER_ID_2_USERNAME))
                .email(new Email(USER_ID_2_EMAIL))
                .names(new Names(USER_ID_2_FIRSTNAME, USER_ID_2_LASTNAME))
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD, "4321", true)))
                .attributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_2)))
                .roles(List.of(new UserRole(ROLE_ID_2, Role.VENDOR_USER)))
                .build();
        User user_3 = User.builder()
                .userId(new UserId(USER_ID_3))
                .username(new Username(USER_ID_1_USERNAME))
                .email(new Email(USER_ID_1_EMAIL))
                .names(new Names(USER_ID_1_FIRSTNAME, USER_ID_1_LASTNAME))
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD, "1234", false)))
                .attributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_1)))
                .roles(List.of(new UserRole(ROLE_ID_1, Role.VENDOR_USER)))
                .build();
        User user_4 = User.builder()
                .userId(new UserId(USER_ID_3))
                .username(new Username(USER_ID_1_USERNAME))
                .email(new Email(USER_ID_1_EMAIL))
                .names(new Names(USER_ID_1_FIRSTNAME, USER_ID_1_LASTNAME))
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD, "1234", false)))
                .attributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_1)))
                .build();
        User user_5 = User.builder()
                .userId(new UserId(USER_ID_3))
                .username(new Username(USER_ID_1_USERNAME))
                .email(new Email(USER_ID_1_EMAIL))
                .names(new Names(USER_ID_1_FIRSTNAME, USER_ID_1_LASTNAME))
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD, "1234", false)))
                .attributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_1)))
                .roles(List.of(new UserRole(ROLE_ID_1, Role.ADMIN)))
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(user_1);
        when(userRepository.update(any(User.class)))
                .thenReturn(user_1);
        doNothing().when(userRepository).delete(any());
        when(userRepository.findById(new UserId(USER_ID_1)))
                .thenReturn(Optional.of(user_1));
        when(userRepository.findById(new UserId(USER_ID_3)))
                .thenReturn(Optional.of(user_3));
        when(userRepository.findById(new UserId(USER_ID_4)))
                .thenReturn(Optional.of(user_4));
        when(userRepository.findById(new UserId(USER_ID_5)))
                .thenReturn(Optional.of(user_5));
        when(userRepository.findAllByAttributes(validAttributes, PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(user_1, user_2)));
        when(userRepository.findAllByUserField(UserField.FIRST_NAME, USER_ID_1_FIRSTNAME, PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(user_1)));
        when(userRepository.findAllRoles())
                .thenReturn(Optional.of(List.of(new UserRole(ROLE_ID_1, Role.VENDOR_USER),
                        new UserRole(ROLE_ID_2, Role.VENDOR_USER))));
    }

    @Test
    public void testCreateUser_customer() {
        UserResponse userResponse = userApplicationService.createUser(validExecutionUser,
                createUserCommandCustomer,
                List.of(new UserRole(ROLE_ID_1, Role.CUSTOMER)),
                null, true);
        assertEquals(USER_ID_1, userResponse.getUserId());
    }

    @Test
    public void testCreateUser_vendor() {
        UserResponse userResponse = userApplicationService.createUser(validExecutionUser,
                createUserCommandVendor,
                List.of(new UserRole(ROLE_ID_1, Role.VENDOR_USER)),
                null, true);
        assertEquals(USER_ID_1, userResponse.getUserId());
    }

    @Test
    public void testCreateUser_vendor_wrong_role() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService.createUser(validExecutionUser,
                        createUserCommandVendor,
                        List.of(new UserRole(ROLE_ID_1, Role.ADMIN)),
                        null, true));
        assertEquals("Invalid role(s)", userDomainException.getMessage());
    }

    @Test
    public void testCreateUser_vendor_user() {
        UserResponse userResponse = userApplicationService.createUser(validExecutionUser,
                createUserCommandVendorUser, validRoles, validAttributes, true);
        assertEquals(USER_ID_1, userResponse.getUserId());
    }

    @Test
    public void testCreateUser_vendor_user_wrong_execution_user() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService
                        .createUser(new ExecutionUser(new UserId(USER_ID_1), List.of(Role.SU_ADMIN)),
                                createUserCommandVendorUser, validRoles, validAttributes, true));
        assertEquals("Executing role must be " + Role.VENDOR.getValue(), userDomainException.getMessage());
    }

    @Test
    public void testCreateUser_vendor_user_wrong_attributes() {
        UserResponse userResponse = userApplicationService.createUser(validExecutionUser,
                createUserCommandVendorUser, validRoles, null, true);
        assertEquals(USER_ID_1, userResponse.getUserId());
    }

    @Test
    public void testCreateUser_vendor_user_wrong_username() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.createUser(validExecutionUser,
                        createUserCommandWrongUsername, validRoles, null, true));
    }

    @Test
    public void testUpdateUser() {
        UserResponse userResponse = userApplicationService.updateUser(validExecutionUser, updateUserCommandValid);
        assertEquals(USER_ID_1, userResponse.getUserId());
    }

    @Test
    public void testUpdateUser_otherAccount_vendor_user() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(validExecutionUser,
                        UpdateUserCommand.builder()
                                .userId(USER_ID_3)
                                .email("j@mail.com")
                                .enabled(false)
                                .firstName("fir")
                                .lastName("las")
                                .build()));
        assertEquals("Resource does not belong to this user!", userDomainException.getMessage());
    }

    @Test
    public void testUpdateUser_wrong_user_id() {
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userApplicationService.updateUser(validExecutionUser, updateUserCommandWrongUserId));
        assertEquals("User not found!", userNotFoundException.getMessage());
    }

    @Test
    public void testUpdateUser_wrong_invalid_execution_user() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(new ExecutionUser(new UserId(USER_ID_2), List.of(Role.SU_ADMIN)),
                        updateUserCommandValid));
        assertEquals("Executing role not allowed to update this user!",
                userDomainException.getMessage());
    }

    @Test
    public void testUpdateUser_invalid_user_id() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.updateUser(validExecutionUser,
                        UpdateUserCommand.builder()
                                .email("j@mail.com")
                                .enabled(false)
                                .firstName("fir")
                                .lastName("las")
                                .build()));
    }


    @Test
    public void testUpdateUser_user_without_role() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(validExecutionUser,
                        UpdateUserCommand.builder()
                                .userId(USER_ID_4)
                                .email("j@mail.com")
                                .enabled(false)
                                .firstName("fir")
                                .lastName("las")
                                .build()));
        assertEquals("User must have at-least one role", userDomainException.getMessage());
    }

    @Test
    public void testUpdateUser_user_with_invalid_role() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService.updateUser(validExecutionUser,
                        UpdateUserCommand.builder()
                                .userId(USER_ID_5)
                                .email("j@mail.com")
                                .enabled(false)
                                .firstName("fir")
                                .lastName("las")
                                .build()));
        assertEquals("User does not have valid role for update!", userDomainException.getMessage());
    }

    @Test
    public void testUpdateUser_user_not_found() {
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userApplicationService.updateUser(validExecutionUser,
                        UpdateUserCommand.builder()
                                .userId("xyz")
                                .email("j@mail.com")
                                .enabled(false)
                                .firstName("fir")
                                .lastName("las")
                                .build()));
        assertEquals("User not found!", userNotFoundException.getMessage());
    }

    @Test
    public void testDeleteUser() {
        UserResponse userResponse = userApplicationService.deleteUser(new ExecutionUser(new UserId(VENDOR_1), List.of(Role.VENDOR)), deleteUserCommandValid);
        assertEquals(USER_ID_1, userResponse.getUserId());
    }

    @Test
    public void testDeleteUser_wrong_invalid_execution_user() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(new ExecutionUser(new UserId(USER_ID_2), List.of(Role.SU_ADMIN)),
                        DeleteUserCommand.builder()
                                .userId(USER_ID_1)
                                .build()));
        assertEquals("Executing role not allowed to delete this user!",
                userDomainException.getMessage());
    }

    @Test
    public void testDeleteUser_invalid_user_id() {
        assertThrows(ConstraintViolationException.class,
                () -> userApplicationService.deleteUser(validExecutionUser,
                        DeleteUserCommand.builder()
                                .build()));
    }


    @Test
    public void testDeleteUser_user_without_role() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(validExecutionUser,
                        DeleteUserCommand.builder()
                                .userId(USER_ID_4)
                                .build()));
        assertEquals("User must have at-least one role", userDomainException.getMessage());
    }

    @Test
    public void testDeleteUser_user_with_invalid_role() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService.deleteUser(validExecutionUser,
                        DeleteUserCommand.builder()
                                .userId(USER_ID_5)
                                .build()));
        assertEquals("User does not have valid role for delete!", userDomainException.getMessage());
    }

    @Test
    public void testDeleteUser_as_vendor_user() {
        UserDomainException userDomainException = assertThrows(UserDomainException.class,
                () -> userApplicationService
                        .deleteUser(new ExecutionUser(new UserId(USER_ID_3), List.of(Role.VENDOR_USER)),
                                deleteUserCommandValid));
        assertEquals("Executing role not allowed to delete this user!", userDomainException.getMessage());
    }

    @Test
    public void testDeleteUser_user_not_found() {
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class,
                () -> userApplicationService.deleteUser(validExecutionUser,
                        DeleteUserCommand.builder()
                                .userId("xyz")
                                .build()));
        assertEquals("User not found!", userNotFoundException.getMessage());
    }

    @Test
    public void testFetchUserRoles() {
        List<UserRole> userRoles = userApplicationService.fetchUserRoles();
        assertEquals(2, userRoles.size());
        assertEquals(ROLE_ID_2, userRoles.get(1).getId());
    }

    @Test
    public void testFetchUsers() {
        List<UserResponse> users = userApplicationService.fetchUsers(validAttributes, PAGE_NUMBER, PAGE_SIZE);
        assertEquals(2, users.size());
        assertEquals(USER_ID_2, users.get(1).getUserId());
    }

    @Test
    public void testFetchUsers_by_UserField() {
        List<UserResponse> users = userApplicationService.fetchUsers(UserField.FIRST_NAME, USER_ID_1_FIRSTNAME, PAGE_NUMBER, PAGE_SIZE);
        assertFalse(users.isEmpty());
    }

}
