package com.vendor.management.system.user.service.dataaccess;

import com.vendor.management.system.domain.util.Helpers;
import com.vendor.management.system.domain.valueobject.*;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.Role;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.UserRepresentationManager;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.UserRepresentationResponse;
import com.vendor.management.system.user.service.dataaccess.keycloak.mapper.KeycloakDataMapper;
import com.vendor.management.system.user.service.dataaccess.keycloak.service.KeycloakAdmin;
import com.vendor.management.system.user.service.dataaccess.keycloak.service.KeycloakClient;
import com.vendor.management.system.user.service.domain.entity.User;
import com.vendor.management.system.user.service.domain.exception.UserDataAccessException;
import com.vendor.management.system.user.service.domain.ports.output.repository.UserRepository;
import com.vendor.management.system.user.service.domain.valueobject.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = DataAccessTestConfiguration.class)
public class DataAccessTest {
    @Autowired
    KeycloakClient keycloakClient;
    @Autowired
    KeycloakAdmin keycloakAdmin;
    @Autowired
    KeycloakDataMapper keycloakDataMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConjurUserService conjurUserService;

    UserRepresentationManager userRepresentationManager, userRepresentationManagerUpdate;
    String username, userId;
    Map<Pagination, String> pagination;
    Map<Pagination, String> paginationInvalid;
    Map<Attribute, String> attributes;
    List<Role> roles;
    List<String> usernames;
    List<String> emails;
    List<String> userIds;
    List<UserRole> userRoles;
    private final Random random = new Random();
    private final String VENDOR_ID_STR = "vendorId1";
    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 10;

    @BeforeAll
    public void init() {
        pagination = Helpers.parsePagination(0, 10);
        paginationInvalid = Helpers.parsePagination(11, 100);
        attributes = Map.of(Attribute.VENDOR_ID, VENDOR_ID_STR);
        username = "spring-test-" + random.nextInt(1_000, 100_000);
        usernames = new ArrayList<>();
        emails = new ArrayList<>();
        userIds = new ArrayList<>();

        userRepresentationManager = UserRepresentationManager.builder()
                .username(username)
                .email("test-" + random.nextInt(1_000, 100_000) + "@example.org")
                .enabled(true)
                .firstName("test-001-first")
                .lastName("test-001-last")
                .credentials(List.of(UserRepresentationManager.Credential.builder()
                        .type("password")
                        .value("test-001-password")
                        .temporary(false)
                        .build()))
                .attributes(Map.of("createdByVendorId", VENDOR_ID_STR))
                .build();
        userRepresentationManagerUpdate = UserRepresentationManager.builder()
                .email("test-update-" + random.nextInt(1_000, 100_000) + "@example.org")
                .enabled(true)
                .firstName("test-update-001-first")
                .lastName("test-update-001-last")
                .credentials(List.of(UserRepresentationManager.Credential.builder()
                        .type("password")
                        .value("test-001-password")
                        .temporary(false)
                        .build()))
                .attributes(Map.of("createdByVendorId", VENDOR_ID_STR))
                .build();
    }

    @Test
    @Order(0)
    public void testingConjur() {
//        String keycloakBaseUrl = conjur.variables().retrieveSecret("UserServiceMicroservice/keycloakBaseUrl");
        String keycloakBaseUrl = conjurUserService.conjurHost().variables().retrieveSecret("UserServiceMicroservice/keycloakBaseUrl");
        log.info("conjur keycloakBaseUrl from the actual tests here.: {}", keycloakBaseUrl);
    }

    @Test
    @Order(1)
    public void testFetchRoles() {
        roles = keycloakAdmin.fetchRoles().block();
        assertNotNull(roles);
        log.info(roles.toString());

        keycloakDataMapper.mapApplicationRoles(roles);
        assertFalse(roles.isEmpty(), "Roles must be more than 0 after filtering those related to appRoles");
        log.info(roles.toString());
        userRoles = keycloakDataMapper.transformKeycloakRolesToUserRoles(roles);
    }

    @Test
    @Order(2)
    public void testGetAccessToken() {
        String accessToken = keycloakClient.generateAccessToken().block();
        assertNotNull(accessToken);
    }

    @Test
    @Order(3)
    public void testCreateUser() {
        keycloakAdmin.createUser(userRepresentationManager).block();
    }

    @Test
    @Order(4)
    public void testFetchUserByUsername() {
        UserRepresentationResponse userRepresentation_username = keycloakAdmin.fetchUserByUsername(username).block();
        assertNotNull(userRepresentation_username);
        assertNotNull(userRepresentation_username.getId());
        assertNotNull(userRepresentation_username.getUsername());
        log.info(userRepresentation_username.toString());
        userId = userRepresentation_username.getId();
    }

    @Test
    @Order(5)
    public void testFetchUserByUsername_not_found() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.fetchUserByUsername("789").block());
        assertEquals("User not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(6)
    public void testFetchUserByUserId() {
        log.info(userId);
        UserRepresentationResponse userRepresentation_user = keycloakAdmin.fetchUserByUserId(userId).block();
        assertNotNull(userRepresentation_user);
        assertNotNull(userRepresentation_user.getId());
        log.info(userRepresentation_user.toString());
    }

    @Test
    @Order(7)
    public void testFetchUserByUserId_not_found() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.fetchUserByUserId("1ee788bd-ea88-4a17-ac85-1c9f97c12106").block());
        assertEquals("User not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(8)
    public void testFetchUserRoles() {
        List<Role> userRoles = keycloakAdmin.fetchUserRoles(userId).block();
        assertNotNull(userRoles);
        log.info(userRoles.toString());
    }

    @Test
    @Order(9)
    public void testFetchUserRoles_not_found() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.fetchUserRoles("1ee788bd-ea88-4a17-ac85-1c9f97c12106").block());
        assertEquals("User not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(10)
    public void testUpdateUser() {
        keycloakAdmin.updateUser(userId, userRepresentationManagerUpdate).block();
    }

    @Test
    @Order(11)
    public void testUpdateUser_not_found() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.updateUser("1ee788bd-ea88-4a17-ac85-1c9f97c12106", userRepresentationManagerUpdate).block());
        assertEquals("User not found", userDataAccessException.getMessage());
    }

//    @Test
//    @Order(12)
//    public void testSendVerificationEmail() {
//        keycloakAdmin.sendVerificationEmail(userId).block();
//    }

    @Test
    @Order(13)
    public void testSendVerificationEmail_not_found() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.sendVerificationEmail("1ee788bd-ea88-4a17-ac85-1c9f97c12106").block());
        assertEquals("User not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(14)
    public void testFetchUsers() {
        List<UserRepresentationResponse> users = keycloakAdmin.fetchUsers(pagination, attributes).block();
        assertNotNull(users);
        log.info(users.toString());
    }

    @Test
    @Order(14)
    public void testFetchUsers_by_firstName() {
        List<UserRepresentationResponse> users = keycloakAdmin.fetchUsers(pagination, attributes, Map.of(UserField.FIRST_NAME.getValue(), "test")).block();
        assertNotNull(users);
        log.info(users.toString());
    }

    @Test
    @Order(15)
    public void testFetchUsers_null_pagination() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.fetchUsers(null, attributes).block());
        assertEquals("Pagination is required!", userDataAccessException.getMessage());
    }

    @Test
    @Order(16)
    public void testFetchUsers_empty_pagination() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.fetchUsers(new HashMap<>(), attributes).block());
        assertEquals("Pagination is required!", userDataAccessException.getMessage());
    }

    @Test
    @Order(17)
    public void testFetchUsers_not_found() {
        log.info(paginationInvalid.toString());
        List<UserRepresentationResponse> users = keycloakAdmin.fetchUsers(paginationInvalid, attributes).block();
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    @Order(18)
    public void testAssignRolesToUser() {
        keycloakAdmin.assignRolesToUser(userId, List.of(roles.get(2))).block();
    }

    @Test
    @Order(19)
    public void testAssignRolesToUser_invalid_role() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.assignRolesToUser(userId, List.of(Role.builder()
                                .id("5463bfd1-b5c9-44c2-b98f-14b0b932c14a")
                                .name("rand-role")
                                .build()))
                        .block());
        assertEquals("Role not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(20)
    public void testAssignRolesToUser_invalid_user_id() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.assignRolesToUser("efbd56b7-499c-4dd8-94c5-f29114be948a", List.of(roles.get(2))).block());
        assertEquals("User not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(21)
    public void testDeleteUser() {
        keycloakAdmin.deleteUser(userId).block();
    }

    @Test
    @Order(22)
    public void testDeleteUser_not_found() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> keycloakAdmin.fetchUserRoles("1ee788bd-ea88-4a17-ac85-1c9f97c12106").block());
        assertEquals("User not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(23)
    public void testSaveUser_UserRepository() {
        int rndNum = random.nextInt(1_000, 100_000);
        usernames.add("spring-test-impl-" + rndNum);
        emails.add("test-impl-" + rndNum + "@example.org");
        User user = User.builder()
                .username(new Username(usernames.get(0)))
                .email(new Email(emails.get(0)))
                .enabled(true)
                .emailVerified(false)
                .names(new Names("test-impl-" + rndNum + "-first", "test-impl-" + rndNum + "-last"))
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD, "1234", false)))
                .attributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_ID_STR)))
                .roles(userRoles)
                .build();
        User savedUser = userRepository.save(user);
        userIds.add(savedUser.getId().getValue());
        assertNotNull(savedUser);
        assertEquals(usernames.get(0), savedUser.getUsername().getValue());
    }

    @Test
    @Order(24)
    public void testSaveUser_UserRepository_without_credentials() {
        int rndNum = random.nextInt(1_000, 100_000);
        usernames.add("spring-test-impl-" + rndNum);
        emails.add("test-impl-" + rndNum + "@example.org");
        User user = User.builder()
                .username(new Username(usernames.get(1)))
                .email(new Email(emails.get(1)))
                .enabled(true)
                .emailVerified(false)
                .names(new Names("test-impl-" + rndNum + "-first", "test-impl-" + rndNum + "-last"))
                .credentials(null)
                .attributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_ID_STR)))
                .roles(userRoles)
                .build();
        User savedUser = userRepository.save(user);
        userIds.add(savedUser.getId().getValue());
        assertNotNull(savedUser);
        assertEquals(usernames.get(1), savedUser.getUsername().getValue());
    }

    @Test
    @Order(25)
    public void testSaveUser_UserRepository_without_attributes() {
        int rndNum = random.nextInt(1_000, 100_000);
        usernames.add("spring-test-impl-" + rndNum);
        emails.add("test-impl-" + rndNum + "@example.org");
        User user = User.builder()
                .username(new Username(usernames.get(2)))
                .email(new Email(emails.get(2)))
                .enabled(true)
                .emailVerified(false)
                .names(new Names("test-impl-" + rndNum + "-first", "test-impl-" + rndNum + "-last"))
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD, "1234", false)))
                .attributes(null)
                .roles(userRoles)
                .build();
        User savedUser = userRepository.save(user);
        userIds.add(savedUser.getId().getValue());
        assertNotNull(savedUser);
        assertEquals(usernames.get(2), savedUser.getUsername().getValue());
    }

    @Test
    @Order(26)
    public void testSaveUser_UserRepository_without_attributes_and_without_credentials() {
        int rndNum = random.nextInt(1_000, 100_000);
        usernames.add("spring-test-impl-" + rndNum);
        emails.add("test-impl-" + rndNum + "@example.org");
        User user = User.builder()
                .username(new Username(usernames.get(3)))
                .email(new Email(emails.get(3)))
                .enabled(true)
                .emailVerified(false)
                .names(new Names("test-impl-" + rndNum + "-first", "test-impl-" + rndNum + "-last"))
                .credentials(null)
                .attributes(null)
                .roles(userRoles)
                .build();
        User savedUser = userRepository.save(user);
        userIds.add(savedUser.getId().getValue());
        assertNotNull(savedUser);
        assertEquals(usernames.get(3), savedUser.getUsername().getValue());
    }

    @Test
    @Order(27)
    public void testSaveUser_UserRepository_without_roles() {
        int rndNum = random.nextInt(1_000, 100_000);
        User user = User.builder()
                .username(new Username("spring-test-impl-" + rndNum))
                .email(new Email("test-impl-" + rndNum + "@example.org"))
                .enabled(true)
                .emailVerified(false)
                .names(new Names("test-impl-" + rndNum + "-first", "test-impl-" + rndNum + "-last"))
                .credentials(List.of(new UserCredential(CredentialType.PASSWORD, "1234", false)))
                .attributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_ID_STR)))
                .roles(null)
                .build();
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> userRepository.save(user));
        assertEquals("User does not have valid role(s)", userDataAccessException.getMessage());
    }

    @Test
    @Order(28)
    public void testUpdateUser_UserRepository() {
        int rndNum = random.nextInt(1_000, 100_000);
        emails.add("test-impl-upd-" + rndNum + "@example.org");
        String userId = userIds.get(0);
        User user = User.builder()
                .userId(new UserId(userId))
                .email(new Email(emails.get(4)))
                .enabled(true)
                .emailVerified(true)
                .names(new Names("test-impl-upd-" + rndNum + "-first", "test-impl-upd-" + rndNum + "-last"))
                .credentials(null)
                .attributes(null)
                .roles(null)
                .build();
        User updatedUser = userRepository.update(user);
        assertNotNull(updatedUser);
        assertEquals(userId, updatedUser.getId().getValue());
    }

    @Test
    @Order(29)
    public void testUpdateUser_UserRepository_invalid_userId() {
        int rndNum = random.nextInt(1_000, 100_000);
        emails.add("test-impl-upd-" + rndNum + "@example.org");
        User user = User.builder()
                .userId(new UserId("85cd2413-117e-4332-a4ee-b6a4943452e4"))
                .email(new Email(emails.get(5)))
                .enabled(true)
                .emailVerified(true)
                .names(new Names("test-impl-upd-" + rndNum + "-first", "test-impl-upd-" + rndNum + "-last"))
                .credentials(null)
                .attributes(null)
                .roles(null)
                .build();
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> userRepository.update(user));
        assertEquals("User not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(30)
    public void testUpdateUser_UserRepository_invalid_email() {
        int rndNum = random.nextInt(1_000, 100_000);
//        emails.add("test-impl-upd-" + rndNum + "@example.org");
        String userId = userIds.get(0);
        User user = User.builder()
                .userId(new UserId(userId))
                .email(new Email(emails.get(1)))
                .enabled(true)
                .emailVerified(true)
                .names(new Names("test-impl-upd-" + rndNum + "-first", "test-impl-upd-" + rndNum + "-last"))
                .credentials(null)
                .attributes(null)
                .roles(null)
                .build();
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> userRepository.update(user));
        assertEquals("User exists with same email", userDataAccessException.getMessage());
    }

    @Test
    @Order(31)
    public void testFindById_UserRepository() {
        String userId = userIds.get(0);
        User userFetched = userRepository.findById(new UserId(userId)).get();
        assertNotNull(userFetched);
        assertEquals(userId, userFetched.getId().getValue());
        assertFalse(userFetched.getRoles().isEmpty());
    }

    @Test
    @Order(32)
    public void testFindById_UserRepository_Not_Found() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> userRepository.findById(new UserId("54bd8163-3a50-481b-b46c-7cbc01e2217f")));
        assertEquals("User not found", userDataAccessException.getMessage());
    }

    @Test
    @Order(33)
    public void testFindAllRoles() {
        List<UserRole> userRoles = userRepository.findAllRoles().orElse(Collections.emptyList());
        assertFalse(userRoles.isEmpty());
    }

    @Test
    @Order(34)
    public void testFindAllByAttributes() {
        List<User> users = userRepository.findAllByAttributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, VENDOR_ID_STR)), PAGE_NUMBER, PAGE_SIZE)
                .orElse(Collections.emptyList());
        assertFalse(users.isEmpty());
    }

    @Test
    @Order(34)
    public void testFindAllByUserField() {
        List<User> users = userRepository.findAllByUserField(UserField.FIRST_NAME, "test", PAGE_NUMBER, PAGE_SIZE)
                .orElse(Collections.emptyList());
        assertFalse(users.isEmpty());
    }

    @Test
    @Order(35)
    public void testFindAllByAttributes_null_attributes() {
        List<User> users = userRepository.findAllByAttributes(null, PAGE_NUMBER, PAGE_SIZE)
                .orElse(Collections.emptyList());
        assertFalse(users.isEmpty());
    }

    @Test
    @Order(36)
    public void testFindAllByAttributes_empty_attributes() {
        List<User> users = userRepository.findAllByAttributes(new UserAttribute(Collections.emptyMap()), PAGE_NUMBER, PAGE_SIZE)
                .orElse(Collections.emptyList());
        assertFalse(users.isEmpty());
    }

    @Test
    @Order(37)
    public void testFindAllByAttributes_invalid_attribute() {
        List<User> users = userRepository.findAllByAttributes(new UserAttribute(Map.of(Attribute.VENDOR_ID, "gxyz")), PAGE_NUMBER, PAGE_SIZE)
                .orElse(Collections.emptyList());
        log.info(users.toString());
        log.info(String.valueOf(users.size()));
        assertTrue(users.isEmpty());
    }

    @Test
    @Order(38)
    public void testDelete() {
        for (String userId : userIds) {
            userRepository.delete(User.builder().userId(new UserId(userId)).build());
        }
    }

    @Test
    @Order(39)
    public void testDelete_Not_Found() {
        UserDataAccessException userDataAccessException = assertThrows(UserDataAccessException.class,
                () -> userRepository.delete(User.builder().userId(new UserId("d273fbfe-86e1-4d3a-b897-da8606096c0c")).build()));
        assertEquals("User not found", userDataAccessException.getMessage());
    }

}
