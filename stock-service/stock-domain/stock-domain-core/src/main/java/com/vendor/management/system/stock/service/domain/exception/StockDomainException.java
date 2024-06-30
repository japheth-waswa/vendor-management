package com.vendor.management.system.stock.service.domain.exception;

import com.vendor.management.system.domain.exception.DomainException;

public class StockDomainException extends DomainException {
    public StockDomainException(String message) {
        super(message);
    }

    public StockDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
