package com.vendor.management.system.stock.service.domain.event.product;

import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.domain.event.DomainEvent;

import java.time.ZonedDateTime;

public abstract class ProductEvent implements DomainEvent<Product> {
    private final Product product;
    private final ZonedDateTime createdAt;

    public ProductEvent(Product product, ZonedDateTime createdAt) {
        this.product = product;
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return product;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
