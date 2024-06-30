package com.vendor.management.system.stock.service.dataaccess.product.mapper;

import com.vendor.management.system.stock.service.dataaccess.product.entity.ProductEntity;
import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.valueobject.*;
import com.vendor.management.system.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.vendor.management.system.domain.util.DomainConstants.UTC;

@Component
public class ProductDataAccessMapper {
    public Product productEntityToProduct(ProductEntity productEntity) {
        return Product.builder()
                .productId(new ProductId(productEntity.getId()))
                .vendorId(new VendorId(productEntity.getVendorId()))
                .productCategoryId(new ProductCategoryId(productEntity.getCategoryId()))
                .productName(new ProductName(productEntity.getName()))
                .productDescription(new ProductDescription(productEntity.getDescription()))
                .productStatus(productEntity.getProductStatus())
                .unitPrice(new Money(productEntity.getUnitPrice()))
                .quantity(new Quantity(productEntity.getQuantity()))
                .fileUrl(new FileUrl(productEntity.getFileUrl()))
                .updatedAt(new UpdatedAt(productEntity.getUpdatedAt()))
                .createdAt(new CreatedAt(productEntity.getCreatedAt()))
                .build();
    }

    public ProductEntity productToProductEntity(Product product) {
        return ProductEntity.builder()
                .id(product.getId().getValue())
                .vendorId(product.getVendorId().getValue())
                .categoryId(product.getProductCategoryId().getValue())
                .name(product.getProductName().getValue())
                .description(product.getProductDescription().getValue())
                .unitPrice(product.getUnitPrice().getAmount())
                .quantity(product.getQuantity().getValue())
                .productStatus(product.getProductStatus())
                .fileUrl(product.getFileUrl() != null ? product.getFileUrl().getValue() : null)
                .updatedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .createdAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .build();
    }

    public ProductEntity productToProductEntityUpdate(Product product) {
        return ProductEntity.builder()
                .id(product.getId().getValue())
                .vendorId(product.getVendorId().getValue())
                .categoryId(product.getProductCategoryId().getValue())
                .name(product.getProductName().getValue())
                .description(product.getProductDescription().getValue())
                .unitPrice(product.getUnitPrice().getAmount())
                .quantity(product.getQuantity().getValue())
                .productStatus(product.getProductStatus())
                .fileUrl(product.getFileUrl() != null ? product.getFileUrl().getValue() : null)
                .updatedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .createdAt(product.getCreatedAt().getValue())
                .build();
    }

    public String productSortFieldToProductEntitySortField(ProductSortField productSortField) {
        return switch ((productSortField)) {
            case NAME -> "name";
            case PRICE -> "unitPrice";
            case QUANTITY -> "quantity";
            case STATUS -> "productStatus";
            case CREATED_AT -> "createdAt";
            default -> "updatedAt";
        };
    }
}
