package com.vendor.management.system.user.service.dataaccess.keycloak.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
public class Role {
    private String id;
    private String name;
}
