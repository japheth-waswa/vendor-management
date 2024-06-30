package com.vendor.management.system.stock.service.domain.dto.productcategory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductCategoryListResponse {
    private final long total;
    @NotNull
    @NotEmpty
    private final List<ProductCategoryResponse> list ;
}
