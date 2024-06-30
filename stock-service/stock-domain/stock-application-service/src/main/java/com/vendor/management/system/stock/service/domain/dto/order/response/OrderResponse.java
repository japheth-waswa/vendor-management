package com.vendor.management.system.stock.service.domain.dto.order.response;

import com.vendor.management.system.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    @NotNull
    private final UUID orderId;
    @NotNull
    private OrderStatus orderStatus;
    @NotNull
    private final UUID customerId;
    @NotNull
    private final BigDecimal price;
    private final List<OrderItemResponse> items;
    private final ZonedDateTime updatedAt;
    private final ZonedDateTime createdAt;
}
