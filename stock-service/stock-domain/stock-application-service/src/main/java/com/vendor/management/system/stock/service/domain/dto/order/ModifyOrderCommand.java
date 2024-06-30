package com.vendor.management.system.stock.service.domain.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ModifyOrderCommand {
    @NotNull
    private final UUID orderId;
    @NotNull
    private final UUID productId;
    @NotNull
    @Min(1)
    private final Integer quantity;
}
