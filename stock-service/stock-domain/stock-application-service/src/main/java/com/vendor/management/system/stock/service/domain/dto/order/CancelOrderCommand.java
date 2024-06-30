package com.vendor.management.system.stock.service.domain.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CancelOrderCommand {
    @NotNull
    private final UUID orderId;
    @NotNull
    @NotEmpty
    List<String> messages;
}
