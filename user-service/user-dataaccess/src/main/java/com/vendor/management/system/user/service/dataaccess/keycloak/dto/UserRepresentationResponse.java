package com.vendor.management.system.user.service.dataaccess.keycloak.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@Getter
@Setter
@Builder
public class UserRepresentationResponse {
    private String id;
    private String username;
    private String email;
    private boolean enabled;
    private boolean emailVerified;
    private String firstName;
    private String lastName;
    private Map<String, List<String>> attributes;
}
