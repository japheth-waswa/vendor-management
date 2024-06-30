package com.vendor.management.system.user.service.dataaccess.keycloak.service;

import com.vendor.management.system.domain.valueobject.ConjurUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static com.vendor.management.system.domain.util.DomainConstants.*;


@Component
public class KeycloakService {
    private final ConjurUserService conjurUserService;

    public KeycloakService(ConjurUserService conjurUserService) {
        this.conjurUserService = conjurUserService;
    }

    @Bean
    public KeycloakConfig getKeycloakConfig() {
        return new KeycloakConfig(conjurUserService.conjurHost().variables().retrieveSecret(CONJUR_USER_SERVICE_KEYCLOAK_BASE_URL),
                conjurUserService.conjurHost().variables().retrieveSecret(CONJUR_USER_SERVICE_KEYCLOAK_CLIENT_ID),
                conjurUserService.conjurHost().variables().retrieveSecret(CONJUR_USER_SERVICE_KEYCLOAK_CLIENT_SECRET));
    }
}
