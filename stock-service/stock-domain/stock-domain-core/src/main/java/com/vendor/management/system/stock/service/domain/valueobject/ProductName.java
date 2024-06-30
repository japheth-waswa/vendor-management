package com.vendor.management.system.stock.service.domain.valueobject;

import com.vendor.management.system.stock.service.domain.exception.StockDomainException;
import com.vendor.management.system.domain.valueobject.AbstractValueObject;

public class ProductName extends AbstractValueObject<String> {
    private final String value;

    public ProductName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new StockDomainException("Product name cannot be empty!");
        }
        this.value = value.toLowerCase();
    }

    @Override
    public String getValue() {
        return value;
    }
}
