package com.vendor.management.system.stock.service.domain.dto.productcategory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UpdateProductCategoryCommand {
    @NotNull
    private final UUID productCategoryId;
    @NotNull
    @Size(min = 1, max = 100)
    private final String name;
}
