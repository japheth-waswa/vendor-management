package com.vendor.management.system.stock.service.domain.mapper;

import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryName;
import com.vendor.management.system.domain.valueobject.CreatedAt;
import com.vendor.management.system.domain.valueobject.UpdatedAt;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.dto.productcategory.CreateProductCategoryCommand;
import com.vendor.management.system.stock.service.domain.dto.productcategory.DeleteProductCategoryCommand;
import com.vendor.management.system.stock.service.domain.dto.productcategory.ProductCategoryResponse;
import com.vendor.management.system.stock.service.domain.dto.productcategory.UpdateProductCategoryCommand;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.vendor.management.system.domain.util.DomainConstants.UTC;

@Component
public class ProductCategoryDataMapper {

    public ProductCategory transformCreateProductCategoryCommandToProductCategory(VendorId vendorId, CreateProductCategoryCommand createProductCategoryCommand) {
        return ProductCategory.builder()
                .productCategoryName(new ProductCategoryName(createProductCategoryCommand.getName()))
                .vendorId(vendorId)
                .updatedAt(new UpdatedAt(ZonedDateTime.now(ZoneId.of(UTC))))
                .createdAt(new CreatedAt(ZonedDateTime.now(ZoneId.of(UTC))))
                .build();
    }

    public ProductCategory transformUpdateProductCategoryCommandToProductCategory(VendorId vendorId, UpdateProductCategoryCommand updateProductCategoryCommand,ProductCategory existingProductCategory) {
        return ProductCategory.builder()
                .productCategoryId(new ProductCategoryId(updateProductCategoryCommand.getProductCategoryId()))
                .productCategoryName(new ProductCategoryName(updateProductCategoryCommand.getName()))
                .vendorId(vendorId)
                .updatedAt(new UpdatedAt(ZonedDateTime.now(ZoneId.of(UTC))))
                .createdAt(existingProductCategory.getCreatedAt())
                .build();
    }

    public ProductCategory transformDeleteProductCategoryCommandToProductCategory(VendorId vendorId, DeleteProductCategoryCommand deleteProductCategoryCommand, ProductCategory existingProductCategory) {
        return ProductCategory.builder()
                .productCategoryId(new ProductCategoryId(deleteProductCategoryCommand.getProductCategoryId()))
                .vendorId(vendorId)
                .productCategoryName(existingProductCategory.getProductCategoryName())
                .updatedAt(existingProductCategory.getUpdatedAt())
                .createdAt(existingProductCategory.getCreatedAt())
                .build();
    }

    public ProductCategoryResponse transformProductCategoryToProductCategoryResponse(ProductCategory productCategory) {
        return ProductCategoryResponse.builder()
                .productCategoryId(productCategory.getId().getValue())
                .name(productCategory.getProductCategoryName().getValue())
                .build();
    }
}
