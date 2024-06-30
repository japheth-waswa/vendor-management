package com.vendor.management.system.application.security;

import com.vendor.management.system.domain.valueobject.Role;
import com.vendor.management.system.domain.valueobject.VendorId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;
import java.util.*;

import static com.vendor.management.system.application.security.JwtConverter.ROLE_PREPENDER;

@Configuration
public class SecurityConfig {
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkUri;

    private final JwtAttrProperties jwtAttrProperties;

    public SecurityConfig(JwtAttrProperties jwtAttrProperties) {
        this.jwtAttrProperties = jwtAttrProperties;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkUri).build();
    }

    public Optional<String> getUserId(Authentication authentication) {
        String userId = authentication.getName();
        return Optional.ofNullable(userId);
    }

    private Optional<Jwt> getJwtToken(Principal principal) {
        Jwt jwt;
        try {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
            jwt = jwtToken.getToken();
            return Optional.ofNullable(jwt);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> getUserId(Principal principal) {
        Jwt jwt = getJwtToken(principal).orElse(null);
        if (jwt == null) return Optional.empty();

        String userId = jwt.getSubject();
        return Optional.ofNullable(userId);
    }

    public Optional<String> getNames(Principal principal) {
        Jwt jwt = getJwtToken(principal).orElse(null);
        if (jwt == null) return Optional.empty();

        String names = jwt.getClaim(jwtAttrProperties.getName());
        return Optional.ofNullable(names);
    }

    public Optional<String> getGivenName(Principal principal) {
        Jwt jwt = getJwtToken(principal).orElse(null);
        if (jwt == null) return Optional.empty();

        String givenName = jwt.getClaim(jwtAttrProperties.getGivenName());
        return Optional.ofNullable(givenName);
    }

    public Optional<String> getFamilyName(Principal principal) {
        Jwt jwt = getJwtToken(principal).orElse(null);
        if (jwt == null) return Optional.empty();

        String familyName = jwt.getClaim(jwtAttrProperties.getGivenName());
        return Optional.ofNullable(familyName);
    }

    public Optional<String> getEmail(Principal principal) {
        Jwt jwt = getJwtToken(principal).orElse(null);
        if (jwt == null) return Optional.empty();

        String email = jwt.getClaim(jwtAttrProperties.getEmail());
        return Optional.ofNullable(email);
    }

    public Optional<String> getCreatedBy(Authentication authentication) {
        Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
        String createdBy = jwt.getClaim(jwtAttrProperties.getCreatedBy());
        return Optional.ofNullable(createdBy);
    }

    public List<Role> getAppRoles(Authentication authentication) {
        return getAppRoles(authentication.getAuthorities());
    }

    public List<Role> getAppRoles(Principal principal) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) principal;
        return getAppRoles(jwtToken.getAuthorities());
    }

    private List<Role> getAppRoles(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) return Collections.emptyList();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.contains(ROLE_PREPENDER))
                .map(authority -> authority.replace(ROLE_PREPENDER, ""))
                .filter(Role::containsValue)
                .map(Role::extractRole)
                .toList();
    }

    public VendorId getVendorId(Authentication authentication) {
        List<Role> userAppRoles = getAppRoles(authentication);
        for (Role role : userAppRoles) {
            if (role.equals(Role.VENDOR)) {
                return new VendorId(UUID.fromString(Objects.requireNonNull(getUserId(authentication).orElse(null))));
            } else if (role.equals(Role.VENDOR_USER)) {
                return new VendorId(UUID.fromString(Objects.requireNonNull(getCreatedBy(authentication).orElse(null))));
            }
        }
        return new VendorId(null);
    }
}
