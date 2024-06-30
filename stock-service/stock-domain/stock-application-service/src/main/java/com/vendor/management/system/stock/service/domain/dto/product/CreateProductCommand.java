package com.vendor.management.system.stock.service.domain.dto.product;

import com.vendor.management.system.stock.service.domain.valueobject.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateProductCommand {
    @NotNull
    @NotBlank
    @Size(min = 1,max = 200)
    private final String name;
    @NotNull
    @NotBlank
    @Size(min = 1,max = 300)
    private final String description;
    @NotNull
    @DecimalMin(value = "1")
    private final BigDecimal price;
    @NotNull
    @Min(0)
    private final Integer quantity;
    @NotNull
    private final ProductStatus status;
    @NotNull
    private final UUID categoryId;
}
