package com.vendor.management.system.stock.service.domain.valueobject;

import com.vendor.management.system.stock.service.domain.exception.StockDomainException;
import com.vendor.management.system.domain.valueobject.AbstractValueObject;

public class ProductCategoryName extends AbstractValueObject<String> {
    private final String value;

    public ProductCategoryName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new StockDomainException("Product category name is required");
        }
        this.value = value.toLowerCase();
    }

    @Override
    public String getValue() {
        return value;
    }
}
