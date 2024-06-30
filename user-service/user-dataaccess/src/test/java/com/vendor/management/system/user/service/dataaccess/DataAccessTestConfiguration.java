package com.vendor.management.system.user.service.dataaccess;

import com.vendor.management.system.domain.util.YamlPropertySourceFactory;
import com.vendor.management.system.user.service.dataaccess.adapter.UserRepositoryImpl;
import com.vendor.management.system.user.service.dataaccess.keycloak.mapper.KeycloakDataMapper;
import com.vendor.management.system.user.service.dataaccess.keycloak.service.KeycloakAdmin;
import com.vendor.management.system.user.service.dataaccess.keycloak.service.KeycloakClient;
import com.vendor.management.system.user.service.dataaccess.keycloak.service.KeycloakConfig;
import com.vendor.management.system.user.service.domain.ports.output.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.vendor.management.system.user.service.dataaccess", "com.vendor.management.system.domain"})
@PropertySources({
        @PropertySource(value = "classpath:user-dataaccess-application.yml", factory = YamlPropertySourceFactory.class)
})
public class DataAccessTestConfiguration {

    @Autowired
    KeycloakConfig keycloakConfig;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public KeycloakClient keycloakClient() {
        return new KeycloakClient(keycloakConfig,webClientBuilder());
    }

    @Bean
    public KeycloakDataMapper keycloakDataMapper() {
        return new KeycloakDataMapper();
    }

    @Bean
    public KeycloakAdmin keycloakAdmin() {
        return new KeycloakAdmin(webClientBuilder(), keycloakClient(), keycloakConfig);
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepositoryImpl(keycloakDataMapper(), keycloakAdmin());
    }
}
