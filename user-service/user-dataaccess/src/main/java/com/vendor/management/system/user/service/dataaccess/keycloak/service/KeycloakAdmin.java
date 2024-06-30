package com.vendor.management.system.user.service.dataaccess.keycloak.service;

import com.vendor.management.system.domain.util.Helpers;
import com.vendor.management.system.domain.valueobject.Pagination;
import com.vendor.management.system.user.service.dataaccess.keycloak.KeycloakError;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.Role;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.UserRepresentationManager;
import com.vendor.management.system.user.service.dataaccess.keycloak.dto.UserRepresentationResponse;
import com.vendor.management.system.user.service.domain.exception.UserDataAccessException;
import com.vendor.management.system.user.service.domain.valueobject.Attribute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class KeycloakAdmin {
    @Value("${keycloak.uri-role}")
    private String roleUri;
    @Value("${keycloak.uri-manage-user}")
    private String manageUserUri;
    @Value("${keycloak.uri-single-user-roles}")
    private String singleUserRoleUri;
    @Value("${keycloak.uri-verification-email}")
    private String verificationEmailUri;

    private final static String OFFSET = "first";
    private final static String LIMIT = "max";
    private final static String ATTRS = "q";

    private final WebClient webClient;
    private final KeycloakClient keycloakClient;
    private final KeycloakConfig keycloakConfig;

    public KeycloakAdmin(WebClient.Builder webClientBuilder,
                         KeycloakClient keycloakClient,
                         KeycloakConfig keycloakConfig) {
        this.webClient = webClientBuilder.build();
        this.keycloakClient = keycloakClient;
        this.keycloakConfig = keycloakConfig;
    }

    private String authHeader(String accessToken) {
        return "Bearer " + accessToken;
    }


    public Mono<List<Role>> fetchRoles() {
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.get()
                        .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), roleUri).toString())
                        .header("Authorization", authHeader(accessToken))
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToFlux(Role.class)
                        .collectList())
                .switchIfEmpty(Mono.error(new UserDataAccessException("Roles not found")));
    }

    public Mono<UserRepresentationResponse> fetchUserByUsername(String username) {
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.get()
                        .uri(Helpers.buildUrl(Map.of("username", username), keycloakConfig.baseUrl(), manageUserUri).toString())
                        .header("Authorization", authHeader(accessToken))
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToFlux(UserRepresentationResponse.class)
                        .next())
                .switchIfEmpty(Mono.error(new UserDataAccessException("User not found")));
    }

    public Mono<UserRepresentationResponse> fetchUserByUserId(String userId) {
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.get()
                        .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), manageUserUri, userId).toString())
                        .header("Authorization", authHeader(accessToken))
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToMono(UserRepresentationResponse.class));
    }

    public Mono<List<Role>> fetchUserRoles(String userId) {
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.get()
                        .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), singleUserRoleUri.replace("{userId}", userId)).toString())
                        .header("Authorization", authHeader(accessToken))
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToFlux(Role.class)
                        .collectList())
                .switchIfEmpty(Mono.error(new UserDataAccessException("User roles not found")));
    }

    public Mono<Void> createUser(UserRepresentationManager userRepresentationManager) {
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.post()
                        .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), manageUserUri).toString())
                        .header("Authorization", authHeader(accessToken))
                        .bodyValue(userRepresentationManager)
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToMono(Void.class));
    }

    public Mono<Void> assignRolesToUser(String userId, List<Role> roles) {
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.post()
                        .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), singleUserRoleUri.replace("{userId}", userId)).toString())
                        .header("Authorization", authHeader(accessToken))
                        .bodyValue(roles)
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToMono(Void.class));
    }

    public Mono<Void> updateUser(String userId, UserRepresentationManager userRepresentationManager) {
        userRepresentationManager.setUsername(null);//username not allowed to be updated.
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.put()
                        .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), manageUserUri, userId).toString())
                        .header("Authorization", authHeader(accessToken))
                        .bodyValue(userRepresentationManager)
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToMono(Void.class));
    }

    public Mono<Void> sendVerificationEmail(String userId) {
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.put()
                        .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), verificationEmailUri.replace("{userId}", userId)).toString())
                        .header("Authorization", authHeader(accessToken))
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToMono(Void.class));
    }

    public Mono<List<UserRepresentationResponse>> fetchUsers(Map<Pagination, String> pagination, Map<Attribute, String> attributes) {
        return fetchUsers(pagination, attributes, null);
    }

    public Mono<List<UserRepresentationResponse>> fetchUsers(Map<Pagination, String> pagination, Map<Attribute, String> attributes, Map<String, String> userFieldAttributes) {
        validateFetchUserProps(pagination, attributes);

        Map<String, String> queryParams = new HashMap<>();

        queryParams.put(OFFSET, pagination.get(Pagination.OFFSET));
        queryParams.put(LIMIT, pagination.get(Pagination.LIMIT));

        if (attributes != null && !attributes.isEmpty()) {
            StringJoiner attrs = new StringJoiner(",");
            attributes.forEach((k, v) -> attrs.add(k.getValue() + ":" + v));
            queryParams.put(ATTRS, attrs.toString());
        }

        if (userFieldAttributes != null && !userFieldAttributes.isEmpty()) {
            queryParams.putAll(userFieldAttributes);
        }

        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.get()
                        .uri(Helpers.buildUrl(queryParams, keycloakConfig.baseUrl(), manageUserUri).toString())
                        .header("Authorization", authHeader(accessToken))
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToFlux(UserRepresentationResponse.class)
                        .collectList())
                .switchIfEmpty(Mono.error(new UserDataAccessException("Users not found")));
    }

    private void validateFetchUserProps(Map<Pagination, String> pagination, Map<Attribute, String> attributes) {
        if (pagination == null || pagination.isEmpty() || !pagination.containsKey(Pagination.OFFSET) || !pagination.containsKey(Pagination.LIMIT)) {
            throw new UserDataAccessException("Pagination is required!");
        }
    }

    public Mono<Void> deleteUser(String userId) {
        return keycloakClient.generateAccessToken()
                .flatMap(accessToken -> webClient.delete()
                        .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), manageUserUri, userId).toString())
                        .header("Authorization", authHeader(accessToken))
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(),
                                response -> response.bodyToMono(KeycloakError.class)
                                        .flatMap(error -> Mono.error(new UserDataAccessException(error.error()))))
                        .bodyToMono(Void.class));
    }

}
