package com.vendor.management.system.stock.service.domain.valueobject;

import com.vendor.management.system.domain.valueobject.BaseId;

import java.util.UUID;

public class ProductCategoryId extends BaseId<UUID> {
    public ProductCategoryId(UUID value) {
        super(value);
    }
}
