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
public class UserRepresentationManager {
    private String username;
    private String email;
    private Boolean enabled;
    private Boolean emailVerified;
    private String firstName;
    private String lastName;
    private List<Credential> credentials;
    private Map<String,String> attributes;

    @Getter
    @Setter
    @Builder
    public static class Credential {
        private String type;
        private String value;
        private boolean temporary;
    }
}
