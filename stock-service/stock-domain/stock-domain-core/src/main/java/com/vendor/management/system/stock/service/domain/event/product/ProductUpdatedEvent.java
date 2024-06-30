package com.vendor.management.system.stock.service.domain.event.product;

import com.vendor.management.system.stock.service.domain.entity.Product;

import java.time.ZonedDateTime;

public class ProductUpdatedEvent extends ProductEvent {
    public ProductUpdatedEvent(Product product, ZonedDateTime createdAt) {
        super(product, createdAt);
    }
}
