package com.vendor.management.system.stock.service.dataaccess.productcategory.mapper;

import com.vendor.management.system.stock.service.dataaccess.productcategory.entity.ProductCategoryEntity;
import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryName;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategorySortField;
import com.vendor.management.system.domain.valueobject.CreatedAt;
import com.vendor.management.system.domain.valueobject.UpdatedAt;
import com.vendor.management.system.domain.valueobject.VendorId;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.vendor.management.system.domain.util.DomainConstants.UTC;

@Component
public class ProductCategoryDataAccessMapper {

    public ProductCategory productCategoryEntityToProductCategory(ProductCategoryEntity productCategoryEntity) {
        return ProductCategory.builder()
                .productCategoryId(new ProductCategoryId(productCategoryEntity.getId()))
                .productCategoryName(new ProductCategoryName(productCategoryEntity.getName()))
                .vendorId(new VendorId(productCategoryEntity.getVendorId()))
                .updatedAt(new UpdatedAt(productCategoryEntity.getUpdatedAt()))
                .createdAt(new CreatedAt(productCategoryEntity.getCreatedAt()))
                .build();
    }

    public ProductCategoryEntity productCategoryToProductCategoryEntity(ProductCategory productCategory) {
        return ProductCategoryEntity.builder()
                .id(productCategory.getId().getValue())
                .vendorId(productCategory.getVendorId().getValue())
                .name(productCategory.getProductCategoryName().getValue())
                .updatedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .createdAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .build();
    }

    public ProductCategoryEntity productCategoryToProductCategoryEntityUpdate(ProductCategory productCategory) {
        return ProductCategoryEntity.builder()
                .id(productCategory.getId().getValue())
                .vendorId(productCategory.getVendorId().getValue())
                .name(productCategory.getProductCategoryName().getValue())
                .updatedAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .createdAt(productCategory.getCreatedAt().getValue())
                .build();
    }

    public String productCategorySortFieldToProductCategoryEntitySortField(ProductCategorySortField productCategorySortField) {
        return switch (productCategorySortField) {
            case NAME -> "name";
            case CREATED_AT -> "createdAt";
            default -> "updatedAt";
        };
    }
}
