package com.vendor.management.system.user.service.application.security;

import com.vendor.management.system.application.security.JwtConverter;
import com.vendor.management.system.application.util.UrlHelper;
import com.vendor.management.system.domain.util.Helpers;
import com.vendor.management.system.domain.valueobject.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class UserSecurityConfig {
    private final JwtConverter jwtConverter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration  = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedOrigins(List.of("http://localhost:3001"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","PATCH", "DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source  =new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(corsCustomizer->corsCustomizer.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                        .requestMatchers(HttpMethod.GET, UrlHelper.UserServiceUrl.ROOT).hasRole(Role.VENDOR.getValue())
                        .requestMatchers(HttpMethod.GET, Helpers.buildUrl(null,
                                UrlHelper.UserServiceUrl.ROOT,
                                UrlHelper.UserServiceUrl.CUSTOMERS_LIST).toString()).hasAnyRole(Role.SU_ADMIN.getValue(),
                                Role.ADMIN.getValue(),
                                Role.VENDOR.getValue(),
                                Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.POST, Helpers.buildUrl(null,
                                UrlHelper.UserServiceUrl.ROOT,
                                UrlHelper.UserServiceUrl.CREATE_CUSTOMER).toString()).permitAll()
                        .requestMatchers(HttpMethod.POST, Helpers.buildUrl(null,
                                UrlHelper.UserServiceUrl.ROOT,
                                UrlHelper.UserServiceUrl.CREATE_VENDOR).toString()).permitAll()
                        .requestMatchers(HttpMethod.POST, Helpers.buildUrl(null,
                                UrlHelper.UserServiceUrl.ROOT,
                                UrlHelper.UserServiceUrl.CREATE_VENDOR_USER).toString()).hasRole(Role.VENDOR.getValue())
                        .requestMatchers(HttpMethod.DELETE, UrlHelper.UserServiceUrl.ROOT).hasRole(Role.VENDOR.getValue())
                        .anyRequest().authenticated());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));
        return http.build();
    }
}
