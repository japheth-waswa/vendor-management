package com.vendor.management.system.stock.service.domain.dto.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductListResponse {
    private final long total;
    @NotNull
    @NotEmpty
    private final List<ProductResponse> list ;
}
