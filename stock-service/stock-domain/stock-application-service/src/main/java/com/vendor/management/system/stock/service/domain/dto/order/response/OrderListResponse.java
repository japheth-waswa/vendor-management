package com.vendor.management.system.stock.service.domain.dto.order.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrderListResponse {
    private final long total;
    @NotNull
    @NotEmpty
    private final List<OrderResponse> list;
}
