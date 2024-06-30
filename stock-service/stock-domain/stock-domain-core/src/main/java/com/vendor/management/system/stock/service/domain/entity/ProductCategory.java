package com.vendor.management.system.stock.service.domain.entity;

import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryName;
import com.vendor.management.system.domain.entity.BaseEntity;
import com.vendor.management.system.domain.valueobject.CreatedAt;
import com.vendor.management.system.domain.valueobject.UpdatedAt;
import com.vendor.management.system.domain.valueobject.VendorId;

import java.util.UUID;

public class ProductCategory extends BaseEntity<ProductCategoryId> {
    private final ProductCategoryName productCategoryName;
    private final VendorId vendorId;
    private final CreatedAt createdAt;
    private final UpdatedAt updatedAt;

    public void init() {
        setId(new ProductCategoryId(UUID.randomUUID()));
    }

    public void update() {
        //checks if everything is in order before update
    }

    public void delete() {
        //check if it can be deleted
    }

    private ProductCategory(Builder builder) {
        setId(builder.productCategoryId);
        productCategoryName = builder.productCategoryName;
        vendorId = builder.vendorId;
        createdAt = builder.createdAt;
        updatedAt = builder.updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ProductCategoryName getProductCategoryName() {
        return productCategoryName;
    }

    public VendorId getVendorId() {
        return vendorId;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public UpdatedAt getUpdatedAt() {
        return updatedAt;
    }

    public static final class Builder {
        private ProductCategoryId productCategoryId;
        private ProductCategoryName productCategoryName;
        private VendorId vendorId;
        private CreatedAt createdAt;
        private UpdatedAt updatedAt;

        private Builder() {
        }

        public Builder productCategoryId(ProductCategoryId val) {
            productCategoryId = val;
            return this;
        }

        public Builder productCategoryName(ProductCategoryName val) {
            productCategoryName = val;
            return this;
        }

        public Builder vendorId(VendorId val) {
            vendorId = val;
            return this;
        }

        public Builder createdAt(CreatedAt val) {
            createdAt = val;
            return this;
        }

        public Builder updatedAt(UpdatedAt val) {
            updatedAt = val;
            return this;
        }

        public ProductCategory build() {
            return new ProductCategory(this);
        }
    }

}
