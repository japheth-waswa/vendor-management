package com.vendor.management.system.user.service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private final String userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final Boolean enabled;
}
