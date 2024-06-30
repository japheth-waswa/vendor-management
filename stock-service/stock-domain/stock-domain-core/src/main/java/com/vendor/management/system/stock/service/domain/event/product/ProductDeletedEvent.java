package com.vendor.management.system.stock.service.domain.event.product;

import com.vendor.management.system.stock.service.domain.entity.Product;

import java.time.ZonedDateTime;

public class ProductDeletedEvent extends ProductEvent {
    public ProductDeletedEvent(Product product, ZonedDateTime createdAt) {
        super(product, createdAt);
    }
}
