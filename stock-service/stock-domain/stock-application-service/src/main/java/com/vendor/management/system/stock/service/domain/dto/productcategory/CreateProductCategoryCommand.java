package com.vendor.management.system.stock.service.domain.dto.productcategory;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(onConstructor_ = @__(@JsonCreator))
public class CreateProductCategoryCommand {
    @NotNull
    @Size(min = 1, max = 100)
    private final String name;
}
