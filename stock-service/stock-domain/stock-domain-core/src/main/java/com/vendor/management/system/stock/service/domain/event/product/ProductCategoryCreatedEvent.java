package com.vendor.management.system.stock.service.domain.event.product;

import com.vendor.management.system.stock.service.domain.entity.ProductCategory;

import java.time.ZonedDateTime;

public class ProductCategoryCreatedEvent extends ProductCategoryEvent {
    public ProductCategoryCreatedEvent(ProductCategory productCategory, ZonedDateTime createdAt) {
        super(productCategory, createdAt);
    }
}
