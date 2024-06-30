package com.vendor.management.system.stock.service.application.rest;

import com.vendor.management.system.application.security.SecurityConfig;
import com.vendor.management.system.application.util.UrlHelper;
import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.dto.product.*;
import com.vendor.management.system.stock.service.domain.ports.input.service.StockApplicationService;
import com.vendor.management.system.stock.service.domain.valueobject.ProductName;
import com.vendor.management.system.stock.service.domain.valueobject.ProductSortField;
import com.vendor.management.system.stock.service.domain.valueobject.ProductStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = UrlHelper.StockServiceUrl.ROOT_PRODUCT, produces = "application/vnd.api.v1+json")
public class ProductController {
    private final StockApplicationService stockApplicationService;
    private final SecurityConfig securityConfig;

    public ProductController(StockApplicationService stockApplicationService,
                             SecurityConfig securityConfig) {
        this.stockApplicationService = stockApplicationService;
        this.securityConfig = securityConfig;
    }

    @GetMapping
    public ResponseEntity<ProductListResponse> fetchProducts(Authentication authentication,
                                                             @RequestParam int pageNumber,
                                                             @RequestParam int pageSize,
                                                             @RequestParam(required = false) SortDirection sortDirection,
                                                             @RequestParam(required = false) ProductSortField sortField,
                                                             @RequestParam(required=false) String name,
                                                             @RequestParam(required = false)ProductStatus productStatus) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Searching product with pageNumber: {}, pageSize: {}, sortDirection: {}, sortField: {} & or name: {}",
                pageNumber, pageSize, sortDirection, sortField, name != null ?name:"");
        ProductListResponse products;
        if (sortDirection != null && sortField != null && (name == null || name.isBlank())) {
            products = stockApplicationService.fetchProducts(vendorId, pageNumber, pageSize,
                    List.of(new AbstractSortPayload<>(sortDirection, sortField)));
        } else if (sortDirection != null && sortField != null && productStatus != null) {
            products = stockApplicationService.fetchProducts(vendorId,new ProductName(name),productStatus, pageNumber, pageSize,
                    List.of(new AbstractSortPayload<>(sortDirection, sortField)));
        }else {
            products = stockApplicationService.fetchProducts(vendorId, pageNumber, pageSize);
        }
        log.info("Found: {} products with pageNumber: {}, pageSize: {}, sortDirection: {}, sortField:{} & or name: {}",
                products.getList().size(), pageNumber, pageSize, sortDirection, sortField, name != null ?name:"");
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(Authentication authentication,
                                                         @RequestBody CreateProductCommand createProductCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Creating product with name: {} for vendor: {}", createProductCommand.getName(), vendorId.getValue());
        ProductResponse productResponse = stockApplicationService.createProduct(vendorId, createProductCommand);
        log.info("Create product with id: {}", productResponse.getProductId());
        return ResponseEntity.ok(productResponse);
    }

    @PatchMapping
    public ResponseEntity<ProductResponse> updateProduct(Authentication authentication,
                                                         @RequestBody UpdateProductCommand updateProductCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Updating product with id: {} & name: {} for vendor: {}",
                updateProductCommand.getProductId(),
                updateProductCommand.getName(),
                vendorId.getValue());
        ProductResponse productResponse = stockApplicationService.updateProduct(vendorId, updateProductCommand);
        log.info("Update product with id: {}", productResponse.getProductId());
        return ResponseEntity.ok(productResponse);
    }

    @DeleteMapping
    public ResponseEntity<ProductResponse> deleteProduct(Authentication authentication,
                                                         @RequestBody DeleteProductCommand deleteProductCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Deleting product with id: {} for vendor: {}",
                deleteProductCommand.getProductId(),
                vendorId.getValue());
        ProductResponse productResponse = stockApplicationService.deleteProduct(vendorId, deleteProductCommand);
        log.info("Deleted product with id: {}", productResponse.getProductId());
        return ResponseEntity.ok(productResponse);
    }

}
