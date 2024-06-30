package com.vendor.management.system.domain.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
 class DotenvConfig {
    @Bean
    public Dotenv dotenv() {
        Dotenv dotenv = Dotenv
                .configure()
                .directory("/.env")
                .ignoreIfMissing()
                .load();

        System.setProperty("CONJUR_ACCOUNT", dotenv.get("CONJUR_ACCOUNT"));
        System.setProperty("CONJUR_APPLIANCE_URL", dotenv.get("CONJUR_APPLIANCE_URL"));

        System.setProperty("CONJUR_AUTHN_LOGIN_HOST_DATABASE_CONFIG", dotenv.get("CONJUR_AUTHN_LOGIN_HOST_DATABASE_CONFIG"));
        System.setProperty("CONJUR_AUTHN_API_KEY_HOST_DATABASE_CONFIG", dotenv.get("CONJUR_AUTHN_API_KEY_HOST_DATABASE_CONFIG"));
        System.setProperty("CONJUR_AUTHN_API_KEY_HOST_DATABASE_VERSION", dotenv.get("CONJUR_AUTHN_API_KEY_HOST_DATABASE_VERSION"));

        System.setProperty("CONJUR_AUTHN_LOGIN_HOST_USER_SERVICE", dotenv.get("CONJUR_AUTHN_LOGIN_HOST_USER_SERVICE"));
        System.setProperty("CONJUR_AUTHN_API_KEY_HOST_USER_SERVICE", dotenv.get("CONJUR_AUTHN_API_KEY_HOST_USER_SERVICE"));
        System.setProperty("CONJUR_AUTHN_API_KEY_HOST_USER_VERSION", dotenv.get("CONJUR_AUTHN_API_KEY_HOST_USER_VERSION"));

        return dotenv;
    }
}
