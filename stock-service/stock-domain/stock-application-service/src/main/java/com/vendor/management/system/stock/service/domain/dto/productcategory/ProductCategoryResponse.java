package com.vendor.management.system.stock.service.domain.dto.productcategory;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ProductCategoryResponse {
    @NotNull
    private final UUID productCategoryId;
    @NotNull
    private final String name;
}
