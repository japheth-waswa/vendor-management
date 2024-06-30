package com.vendor.management.system.stock.service.domain.dto.product;

import com.vendor.management.system.stock.service.domain.valueobject.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse {
    @NotNull
    private final UUID productId;
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private final BigDecimal price;
    private final int quantity;
    private final ProductStatus status;
    @NotNull
    private final UUID categoryId;
    private final String fileUrl;
}
