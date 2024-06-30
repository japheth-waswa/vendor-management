package com.vendor.management.system.stock.service.domain.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeleteOrderProductCommand {
    @NotNull
    private final UUID orderId;
    @NotNull
    private final UUID productId;
}
