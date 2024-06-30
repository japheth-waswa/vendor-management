package com.vendor.management.system.stock.service.domain.valueobject;

import com.vendor.management.system.stock.service.domain.exception.StockDomainException;
import com.vendor.management.system.domain.valueobject.AbstractValueObject;

public class ProductDescription extends AbstractValueObject<String> {
    private final String value;

    public ProductDescription(String value) {
        if(value == null || value.trim().isEmpty()){
            throw new StockDomainException("Product description cannot be empty!");
        }
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
