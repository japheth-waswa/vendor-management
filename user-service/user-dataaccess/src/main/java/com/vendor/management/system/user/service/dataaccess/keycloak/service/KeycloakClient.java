package com.vendor.management.system.user.service.dataaccess.keycloak.service;

import com.vendor.management.system.domain.util.Helpers;
import com.vendor.management.system.user.service.dataaccess.keycloak.KeycloakError;
import com.vendor.management.system.user.service.domain.exception.UserDataAccessException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KeycloakClient {
    @Value("${keycloak.uri-token}")
    private String tokenUri;

    private final WebClient webClient;
    private final KeycloakConfig keycloakConfig;

    public KeycloakClient(KeycloakConfig keycloakConfig,
                          WebClient.Builder webClientBuilder) {
        this.keycloakConfig = keycloakConfig;
        this.webClient = webClientBuilder.build();
    }

    public Mono<String> generateAccessToken() {
        return webClient.post()
                .uri(Helpers.buildUrl(null, keycloakConfig.baseUrl(), tokenUri).toString())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("client_id=" + keycloakConfig.clientId() + "&client_secret=" + keycloakConfig.clientSecret() + "&grant_type=client_credentials")
                .retrieve().onStatus(status -> !status.is2xxSuccessful(),
                        response -> response.bodyToMono(KeycloakError.class)
                                .flatMap(error -> Mono.error(new UserDataAccessException(error.getError()))))
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccess_token);
    }

    @Getter
    @Setter
    private static class TokenResponse {
        private String access_token;
    }
}
