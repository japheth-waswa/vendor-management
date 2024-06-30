package com.vendor.management.system.stock.service.domain.mapper;

import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductDescription;
import com.vendor.management.system.stock.service.domain.valueobject.ProductName;
import com.vendor.management.system.domain.valueobject.*;
import com.vendor.management.system.stock.service.domain.dto.product.CreateProductCommand;
import com.vendor.management.system.stock.service.domain.dto.product.ProductResponse;
import com.vendor.management.system.stock.service.domain.dto.product.UpdateProductCommand;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.vendor.management.system.domain.util.DomainConstants.UTC;

@Component
public class ProductDataMapper {
    public Product transformCreateProductCommandToProduct(VendorId vendorId, CreateProductCommand createProductCommand) {
        return Product.builder()
                .vendorId(vendorId)
                .productName(new ProductName(createProductCommand.getName()))
                .productDescription(new ProductDescription(createProductCommand.getDescription()))
                .unitPrice(new Money(createProductCommand.getPrice()))
                .quantity(new Quantity(createProductCommand.getQuantity()))
                .productStatus(createProductCommand.getStatus())
                .productCategoryId(new ProductCategoryId(createProductCommand.getCategoryId()))
                .updatedAt(new UpdatedAt(ZonedDateTime.now(ZoneId.of(UTC))))
                .createdAt(new CreatedAt(ZonedDateTime.now(ZoneId.of(UTC))))
                .build();
    }

    public Product transformUpdateProductCommandToProduct(UpdateProductCommand updateProductCommand, Product existingProduct) {
        return Product.builder()
                .productId(existingProduct.getId())
                .vendorId(existingProduct.getVendorId())
                .productName(new ProductName(updateProductCommand.getName()))
                .productDescription(new ProductDescription(updateProductCommand.getDescription()))
                .unitPrice(new Money(updateProductCommand.getPrice()))
                .quantity(new Quantity(updateProductCommand.getQuantity()))
                .productStatus(updateProductCommand.getStatus())
                .productCategoryId(existingProduct.getProductCategoryId())
                .updatedAt(new UpdatedAt(ZonedDateTime.now(ZoneId.of(UTC))))
                .createdAt(existingProduct.getCreatedAt())
                .build();
    }

    public ProductResponse transformProductToProductResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getId().getValue())
                .name(product.getProductName().getValue())
                .price(product.getUnitPrice().getAmount())
                .quantity(product.getQuantity().getValue())
                .categoryId(product.getProductCategoryId().getValue())
                .description(product.getProductDescription().getValue())
                .status(product.getProductStatus())
                .fileUrl(product.getFileUrl() != null ? product.getFileUrl().getValue() : null)
                .build();
    }
}
