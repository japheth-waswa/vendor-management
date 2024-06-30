package com.vendor.management.system.stock.service.domain.exception;

import com.vendor.management.system.domain.exception.DomainException;

public class ProductCategoryNotFoundException extends DomainException {
    public ProductCategoryNotFoundException(String message) {
        super(message);
    }

    public ProductCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
