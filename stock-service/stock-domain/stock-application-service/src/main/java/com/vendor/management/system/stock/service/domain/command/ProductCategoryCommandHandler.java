package com.vendor.management.system.stock.service.domain.command;

import com.vendor.management.system.stock.service.domain.StockDomainService;
import com.vendor.management.system.stock.service.domain.dto.productcategory.*;
import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.stock.service.domain.exception.ProductCategoryNotFoundException;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategorySortField;
import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.mapper.ProductCategoryDataMapper;
import com.vendor.management.system.stock.service.domain.ports.output.repository.ProductCategoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductCategoryCommandHandler {

    private final StockDomainService stockDomainService;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductCategoryDataMapper productCategoryDataMapper;

    public ProductCategoryCommandHandler(StockDomainService stockDomainService,
                                         ProductCategoryRepository productCategoryRepository,
                                         ProductCategoryDataMapper productCategoryDataMapper) {
        this.stockDomainService = stockDomainService;
        this.productCategoryRepository = productCategoryRepository;
        this.productCategoryDataMapper = productCategoryDataMapper;
    }

    public ProductCategoryResponse createProductCategory(VendorId vendorId, CreateProductCategoryCommand createProductCategoryCommand) {
        ProductCategory productCategory = productCategoryDataMapper.transformCreateProductCategoryCommandToProductCategory(vendorId, createProductCategoryCommand);
        stockDomainService.createProductCategory(productCategory);
        return productCategoryDataMapper.transformProductCategoryToProductCategoryResponse(productCategoryRepository.save(productCategory));
    }

    public ProductCategoryResponse updateProductCategory(VendorId vendorId, UpdateProductCategoryCommand updateProductCategoryCommand) {
        ProductCategory existingProductCategory = getExistingProductCategory(vendorId, new ProductCategoryId(updateProductCategoryCommand.getProductCategoryId()));
        ProductCategory productCategory = productCategoryDataMapper.transformUpdateProductCategoryCommandToProductCategory(vendorId, updateProductCategoryCommand, existingProductCategory);
        stockDomainService.updateProductCategory(productCategory);
        return productCategoryDataMapper.transformProductCategoryToProductCategoryResponse(productCategoryRepository.update(productCategory));
    }

    public ProductCategoryResponse deleteProductCategory(VendorId vendorId, DeleteProductCategoryCommand deleteProductCategoryCommand) {
        ProductCategory existingProductCategory = getExistingProductCategory(vendorId, new ProductCategoryId(deleteProductCategoryCommand.getProductCategoryId()));
        ProductCategory productCategory = productCategoryDataMapper.transformDeleteProductCategoryCommandToProductCategory(vendorId, deleteProductCategoryCommand, existingProductCategory);
        stockDomainService.deleteProductCategory(productCategory);
        productCategoryRepository.delete(productCategory.getId(), productCategory.getVendorId());
        return productCategoryDataMapper.transformProductCategoryToProductCategoryResponse(productCategory);
    }

    public ProductCategoryListResponse fetchProductCategories(VendorId vendorId, int pageNumber, int pageSize) {
        return productCategories(productCategoryRepository
                        .findAll(vendorId, pageNumber, pageSize)
                        .orElseThrow(() -> new ProductCategoryNotFoundException("Product categories not found!")),
                productCategoryRepository.countAll(vendorId));
    }

    public ProductCategoryListResponse fetchProductCategories(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductCategorySortField>> sort) {
        return productCategories(productCategoryRepository
                        .findAll(vendorId, pageNumber, pageSize, sort)
                        .orElseThrow(() -> new ProductCategoryNotFoundException("Product categories not found!")),
                productCategoryRepository.countAll(vendorId));
    }

    private ProductCategory getExistingProductCategory(VendorId vendorId, ProductCategoryId productCategoryId) {
        return productCategoryRepository
                .findByIdAndVendorId(productCategoryId, vendorId)
                .orElseThrow(() -> new ProductCategoryNotFoundException("Product category not found!"));
    }

    private ProductCategoryListResponse productCategories(List<ProductCategory> productCategories, long total) {
        return ProductCategoryListResponse.builder()
                .list(productCategories.stream()
                        .map(productCategoryDataMapper::transformProductCategoryToProductCategoryResponse)
                        .toList())
                .total(total)
                .build();
    }
}
