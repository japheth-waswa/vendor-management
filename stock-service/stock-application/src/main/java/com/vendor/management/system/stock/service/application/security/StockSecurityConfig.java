package com.vendor.management.system.stock.service.application.security;

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
public class StockSecurityConfig {
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
                        .requestMatchers(HttpMethod.GET, UrlHelper.StockServiceUrl.ROOT_PRODUCT_CATEGORY).hasRole(Role.VENDOR.getValue())
                        .requestMatchers(HttpMethod.POST, UrlHelper.StockServiceUrl.ROOT_PRODUCT_CATEGORY).hasRole(Role.VENDOR.getValue())
                        .requestMatchers(HttpMethod.PATCH, UrlHelper.StockServiceUrl.ROOT_PRODUCT_CATEGORY).hasRole(Role.VENDOR.getValue())
                        .requestMatchers(HttpMethod.DELETE, UrlHelper.StockServiceUrl.ROOT_PRODUCT_CATEGORY).hasRole(Role.VENDOR.getValue())

                        .requestMatchers(HttpMethod.GET, UrlHelper.StockServiceUrl.ROOT_PRODUCT).hasAnyRole(Role.VENDOR.getValue(), Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.POST, UrlHelper.StockServiceUrl.ROOT_PRODUCT).hasRole(Role.VENDOR.getValue())
                        .requestMatchers(HttpMethod.PATCH, UrlHelper.StockServiceUrl.ROOT_PRODUCT).hasRole(Role.VENDOR.getValue())
                        .requestMatchers(HttpMethod.DELETE, UrlHelper.StockServiceUrl.ROOT_PRODUCT).hasRole(Role.VENDOR.getValue())

                        .requestMatchers(HttpMethod.GET, UrlHelper.StockServiceUrl.ROOT_ORDER).hasAnyRole(Role.VENDOR.getValue(), Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.POST, UrlHelper.StockServiceUrl.ROOT_ORDER).hasAnyRole(Role.VENDOR.getValue(), Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.PATCH, UrlHelper.StockServiceUrl.ROOT_ORDER).hasAnyRole(Role.VENDOR.getValue(), Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.DELETE, UrlHelper.StockServiceUrl.ROOT_ORDER).hasRole(Role.VENDOR.getValue())
                        .requestMatchers(HttpMethod.GET, Helpers.buildUrl(null,
                                UrlHelper.StockServiceUrl.ROOT_ORDER,
                                UrlHelper.StockServiceUrl.ORDER_LIST).toString()).hasAnyRole(Role.VENDOR.getValue(), Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.PATCH, Helpers.buildUrl(null,
                                UrlHelper.StockServiceUrl.ROOT_ORDER,
                                UrlHelper.StockServiceUrl.ORDER_SETTLE).toString()).hasAnyRole(Role.VENDOR.getValue(), Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.PATCH, Helpers.buildUrl(null,
                                UrlHelper.StockServiceUrl.ROOT_ORDER,
                                UrlHelper.StockServiceUrl.ORDER_CANCEL).toString()).hasAnyRole(Role.VENDOR.getValue(), Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.DELETE, Helpers.buildUrl(null,
                                UrlHelper.StockServiceUrl.ROOT_ORDER,
                                UrlHelper.StockServiceUrl.ORDER_REMOVE_PRODUCT).toString()).hasAnyRole(Role.VENDOR.getValue(), Role.VENDOR_USER.getValue())
                        .requestMatchers(HttpMethod.DELETE, Helpers.buildUrl(null,
                                UrlHelper.StockServiceUrl.ROOT_ORDER,
                                UrlHelper.StockServiceUrl.ORDER_REMOVE_ITEM).toString()).hasAnyRole(Role.VENDOR.getValue())
                        .anyRequest().authenticated());
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));
        return http.build();
    }
}
