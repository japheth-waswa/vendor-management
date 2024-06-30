package com.vendor.management.system.user.service.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(onConstructor_ = @__(@JsonCreator))
public class DeleteUserCommand {
    @NotNull
    @NotBlank
    private final String userId;
}
