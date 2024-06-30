package com.vendor.management.system.stock.service.domain.entity;

import com.vendor.management.system.stock.service.domain.exception.StockDomainException;
import com.vendor.management.system.stock.service.domain.valueobject.*;
import com.vendor.management.system.domain.entity.AggregateRoot;
import com.vendor.management.system.domain.valueobject.*;

import java.util.UUID;

public class Product extends AggregateRoot<ProductId> {
    private final VendorId vendorId;
    private ProductCategoryId productCategoryId;
    private final ProductName productName;
    private final ProductDescription productDescription;
    private final Money unitPrice;
    private final Quantity quantity;
    private final ProductStatus productStatus;
    private final FileUrl fileUrl;
    private final CreatedAt createdAt;
    private final UpdatedAt updatedAt;

    public void init(ProductCategory productCategory) {
        setId(new ProductId(UUID.randomUUID()));
        this.productCategoryId = productCategory.getId();
        validateRequiredFields();
        validateProductCategoryId(productCategory);
    }

    public void update() {
        validateRequiredFields();
    }

    public void update(ProductCategory productCategory) {
        this.productCategoryId = productCategory.getId();
        validateRequiredFields();
        validateProductCategoryId(productCategory);
    }

    public void delete() {
        //check if it can be deleted
    }

    private void validateRequiredFields() {
        validateProductId();
        validateProductName();
        validateProductDescription();
        validateUnitPrice();
        validateQuantity();
        validateProductStatus();
        validateProductCategoryId();
    }

    private void validateProductId() {
        if (getId() == null || getId().getValue() == null) {
            throw new StockDomainException("Product Id is required!");
        }
    }

    private void validateProductName() {
        if (productName == null || productName.getValue() == null) {
            throw new StockDomainException("Product name is required!");
        }
    }

    private void validateProductDescription() {
        if (productDescription == null || productDescription.getValue() == null) {
            throw new StockDomainException("Product description is required!");
        }
    }

    private void validateUnitPrice() {
        if (unitPrice == null || !unitPrice.isGreaterThanZero()) {
            throw new StockDomainException("Unit price must be greater than zero!");
        }
    }

    private void validateQuantity() {
        if (quantity == null || quantity.getValue() == null) {
            throw new StockDomainException("Quantity is required!");
        }
    }

    private void validateProductStatus() {
        if (productStatus == null) {
            throw new StockDomainException("Product status is required!");
        }
    }

    private void validateProductCategoryId() {
        if (productCategoryId == null || productCategoryId.getValue() == null) {
            throw new StockDomainException("Product category id is required!");
        }
    }

    private void validateProductCategoryId(ProductCategory productCategory) {
        if (!productCategory.getVendorId().equals(vendorId)) {
            throw new StockDomainException("Product category does not belong to the current product vendor!");
        }
    }

    public VendorId getVendorId() {
        return vendorId;
    }

    public ProductName getProductName() {
        return productName;
    }

    public ProductDescription getProductDescription() {
        return productDescription;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public ProductCategoryId getProductCategoryId() {
        return productCategoryId;
    }

    public FileUrl getFileUrl() {
        return fileUrl;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public UpdatedAt getUpdatedAt() {
        return updatedAt;
    }

    private Product(Builder builder) {
        setId(builder.productId);
        vendorId = builder.vendorId;
        productName = builder.productName;
        productDescription = builder.productDescription;
        unitPrice = builder.unitPrice;
        quantity = builder.quantity;
        productStatus = builder.productStatus;
        productCategoryId = builder.productCategoryId;
        fileUrl = builder.fileUrl;
        createdAt = builder.createdAt;
        updatedAt = builder.updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ProductId productId;
        private VendorId vendorId;
        private ProductName productName;
        private ProductDescription productDescription;
        private Money unitPrice;
        private Quantity quantity;
        private ProductStatus productStatus;
        private ProductCategoryId productCategoryId;
        private FileUrl fileUrl;
        private CreatedAt createdAt;
        private UpdatedAt updatedAt;

        private Builder() {
        }

        public Builder productId(ProductId val) {
            productId = val;
            return this;
        }

        public Builder vendorId(VendorId val) {
            vendorId = val;
            return this;
        }

        public Builder productName(ProductName val) {
            productName = val;
            return this;
        }

        public Builder productDescription(ProductDescription val) {
            productDescription = val;
            return this;
        }

        public Builder unitPrice(Money val) {
            unitPrice = val;
            return this;
        }

        public Builder quantity(Quantity val) {
            quantity = val;
            return this;
        }

        public Builder productStatus(ProductStatus val) {
            productStatus = val;
            return this;
        }

        public Builder productCategoryId(ProductCategoryId val) {
            productCategoryId = val;
            return this;
        }

        public Builder fileUrl(FileUrl val) {
            fileUrl = val;
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

        public Product build() {
            return new Product(this);
        }
    }
}
