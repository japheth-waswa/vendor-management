package com.vendor.management.system.stock.service.domain.event.product;

import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.domain.event.DomainEvent;

import java.time.ZonedDateTime;

public abstract class ProductCategoryEvent implements DomainEvent<ProductCategory> {
    private final ProductCategory productCategory;
    private final ZonedDateTime createdAt;

    public ProductCategoryEvent(ProductCategory productCategory, ZonedDateTime createdAt) {
        this.productCategory = productCategory;
        this.createdAt = createdAt;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
