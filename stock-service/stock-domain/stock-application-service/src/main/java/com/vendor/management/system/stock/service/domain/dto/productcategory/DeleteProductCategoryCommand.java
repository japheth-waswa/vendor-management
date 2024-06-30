package com.vendor.management.system.stock.service.domain.dto.productcategory;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(onConstructor_ = @__(@JsonCreator))
public class DeleteProductCategoryCommand {
    @NotNull
    private final UUID productCategoryId;
}
