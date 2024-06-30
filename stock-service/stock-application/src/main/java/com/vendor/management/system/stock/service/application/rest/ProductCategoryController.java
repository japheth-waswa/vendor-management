package com.vendor.management.system.stock.service.application.rest;

import com.vendor.management.system.application.security.SecurityConfig;
import com.vendor.management.system.application.util.UrlHelper;
import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.dto.productcategory.*;
import com.vendor.management.system.stock.service.domain.ports.input.service.StockApplicationService;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategorySortField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = UrlHelper.StockServiceUrl.ROOT_PRODUCT_CATEGORY, produces = "application/vnd.api.v1+json")
public class ProductCategoryController {
    private final StockApplicationService stockApplicationService;
    private final SecurityConfig securityConfig;

    public ProductCategoryController(StockApplicationService stockApplicationService,
                                     SecurityConfig securityConfig) {
        this.stockApplicationService = stockApplicationService;
        this.securityConfig = securityConfig;
    }

    @PostMapping
    public ResponseEntity<ProductCategoryResponse> createProductCategory(Authentication authentication,
                                                                         @RequestBody CreateProductCategoryCommand createProductCategoryCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Creating product category with name: {} for vendor: {}", createProductCategoryCommand.getName(), vendorId.getValue().toString());
        ProductCategoryResponse productCategoryResponse = stockApplicationService.createProductCategory(vendorId, createProductCategoryCommand);
        log.info("Created product category with id: {}", productCategoryResponse.getProductCategoryId());
        return ResponseEntity.ok(productCategoryResponse);
    }

    @PatchMapping
    public ResponseEntity<ProductCategoryResponse> updateProductCategory(Authentication authentication,
                                                                         @RequestBody UpdateProductCategoryCommand updateProductCategoryCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Updating product category with id: {} & name: {} for vendor: {}",
                updateProductCategoryCommand.getProductCategoryId(),
                updateProductCategoryCommand.getName(),
                vendorId.getValue().toString());
        ProductCategoryResponse productCategoryResponse = stockApplicationService.updateProductCategory(vendorId, updateProductCategoryCommand);
        log.info("Updated product category with id: {}", productCategoryResponse.getProductCategoryId());
        return ResponseEntity.ok(productCategoryResponse);
    }

    @DeleteMapping
    public ResponseEntity<ProductCategoryResponse> deleteProductCategory(Authentication authentication,
                                                                         @RequestBody DeleteProductCategoryCommand deleteProductCategoryCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Deleting product category with id: {} for vendor: {}",
                deleteProductCategoryCommand.getProductCategoryId(),
                vendorId.getValue().toString());
        ProductCategoryResponse productCategoryResponse = stockApplicationService.deleteProductCategory(vendorId, deleteProductCategoryCommand);
        log.info("Deleted product category with id: {}", productCategoryResponse.getProductCategoryId());
        return ResponseEntity.ok(productCategoryResponse);
    }

    @GetMapping
    public ResponseEntity<ProductCategoryListResponse> fetchProductCategories(Authentication authentication,
                                                                              @RequestParam int pageNumber,
                                                                              @RequestParam int pageSize,
                                                                              @RequestParam(required = false) SortDirection sortDirection,
                                                                              @RequestParam(required = false) ProductCategorySortField sortField) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Searching product categories with pageNumber: {}, pageSize: {} sortDirection: {} sortField: {}",
                pageNumber, pageSize, sortDirection, sortField);
        ProductCategoryListResponse productCategories;
        if (sortDirection != null && sortField != null) {
            productCategories = stockApplicationService.fetchCategories(vendorId, pageNumber, pageSize, List.of(new AbstractSortPayload<>(sortDirection, sortField)));
        } else {
            productCategories = stockApplicationService.fetchCategories(vendorId, pageNumber, pageSize);
        }
        log.info("Found: {} product categories with pageNumber: {}, pageSize: {} sortDirection: {} sortField: {}",
                productCategories.getList().size(), pageNumber, pageSize, sortDirection, sortField);
        return ResponseEntity.ok(productCategories);
    }

}
