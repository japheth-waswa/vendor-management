package com.vendor.management.system.user.service.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class UpdateUserCommand {
    @NotNull
    @NotBlank
    private final String userId;
    @Email
    @Size(max = 100)
    private final String email;
    @NotNull
    @NotBlank
    @Size(min = 3,max = 20)
    private final String firstName;
    @NotNull
    @NotBlank
    @Size(min = 3,max = 20)
    private final String lastName;
    @NotNull
    private final Boolean enabled;
}
