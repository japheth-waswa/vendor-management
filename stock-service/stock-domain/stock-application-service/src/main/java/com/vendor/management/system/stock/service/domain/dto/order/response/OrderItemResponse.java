package com.vendor.management.system.stock.service.domain.dto.order.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponse {
    @NotNull
    private final Long orderItemId;
    @NotNull
    private final UUID productId;
    @NotNull
    private final String productName;
    @NotNull
    private final Integer quantity;
    @NotNull
    private final BigDecimal price;
    @NotNull
    private final BigDecimal subTotal;
}
