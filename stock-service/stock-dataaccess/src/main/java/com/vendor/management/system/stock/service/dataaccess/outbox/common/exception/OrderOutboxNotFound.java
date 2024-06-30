package com.vendor.management.system.stock.service.dataaccess.outbox.common.exception;

public class OrderOutboxNotFound extends RuntimeException{
    public OrderOutboxNotFound(String message) {
        super(message);
    }
}
