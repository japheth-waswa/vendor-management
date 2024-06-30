package com.vendor.management.system.stock.service.domain.valueobject;

import com.vendor.management.system.domain.valueobject.BaseId;

import java.util.UUID;

public class ProductId extends BaseId<UUID> {
    public ProductId(UUID value) {
        super(value);
    }
}
