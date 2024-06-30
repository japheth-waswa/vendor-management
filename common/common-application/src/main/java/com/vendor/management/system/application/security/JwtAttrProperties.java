package com.vendor.management.system.application.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt.attr")
public class JwtAttrProperties {
    private String username;
    private String email;
    private String givenName;
    private String familyName;
    private String name;
    private String createdBy;
}
