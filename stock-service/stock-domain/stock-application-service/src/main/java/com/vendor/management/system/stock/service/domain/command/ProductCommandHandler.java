package com.vendor.management.system.stock.service.domain.command;

import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.StockDomainService;
import com.vendor.management.system.stock.service.domain.dto.product.*;
import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.stock.service.domain.exception.ProductCategoryNotFoundException;
import com.vendor.management.system.stock.service.domain.exception.ProductNotFoundException;
import com.vendor.management.system.stock.service.domain.mapper.ProductDataMapper;
import com.vendor.management.system.stock.service.domain.ports.output.repository.ProductCategoryRepository;
import com.vendor.management.system.stock.service.domain.ports.output.repository.ProductRepository;
import com.vendor.management.system.stock.service.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductCommandHandler {
    private final StockDomainService stockDomainService;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductDataMapper productDataMapper;

    public ProductCommandHandler(StockDomainService stockDomainService,
                                 ProductRepository productRepository,
                                 ProductCategoryRepository productCategoryRepository,
                                 ProductDataMapper productDataMapper) {
        this.stockDomainService = stockDomainService;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productDataMapper = productDataMapper;
    }

    public ProductResponse createProduct(VendorId vendorId, CreateProductCommand createProductCommand) {
        Product product = productDataMapper.transformCreateProductCommandToProduct(vendorId, createProductCommand);
        stockDomainService.createProduct(product, fetchCategory(vendorId, new ProductCategoryId(createProductCommand.getCategoryId())));
        return productDataMapper.transformProductToProductResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(VendorId vendorId, UpdateProductCommand updateProductCommand) {
        Product product = productDataMapper.transformUpdateProductCommandToProduct(updateProductCommand,
                getExistingProduct(vendorId, new ProductId(updateProductCommand.getProductId())));
        stockDomainService.updateProduct(product, fetchCategory(vendorId, new ProductCategoryId(updateProductCommand.getCategoryId())));
        return productDataMapper.transformProductToProductResponse(productRepository.update(product));
    }

    public ProductResponse deleteProduct(VendorId vendorId, DeleteProductCommand deleteProductCommand) {
        Product product = getExistingProduct(vendorId, new ProductId(deleteProductCommand.getProductId()));
        stockDomainService.deleteProduct(product);
        productRepository.delete(product.getId(), vendorId);
        return productDataMapper.transformProductToProductResponse(product);
    }

    public ProductListResponse fetchProducts(VendorId vendorId, int pageNumber, int pageSize) {
        return products(productRepository
                .findAll(vendorId, pageNumber, pageSize)
                .orElseThrow(() -> new ProductNotFoundException("Products not found!")), productRepository.countAll(vendorId));
    }

    public ProductListResponse fetchProducts(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductSortField>> sort) {
        return products(productRepository
                .findAll(vendorId, pageNumber, pageSize, sort)
                .orElseThrow(() -> new ProductNotFoundException("Products not found!")), productRepository.countAll(vendorId));
    }

    public ProductListResponse fetchProducts(VendorId vendorId, ProductName productName, ProductStatus productStatus, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductSortField>> sort) {
        return products(productRepository
                .findAllByName(vendorId, productName,productStatus, pageNumber, pageSize, sort)
                .orElseThrow(() -> new ProductNotFoundException("Products not found!")), productRepository.countAllByName(vendorId, productName));
    }

    private ProductCategory fetchCategory(VendorId vendorId, ProductCategoryId productCategoryId) {
        return productCategoryRepository.findByIdAndVendorId(productCategoryId, vendorId)
                .orElseThrow(() -> new ProductCategoryNotFoundException("Category does not belong to this vendor!"));
    }

    private Product getExistingProduct(VendorId vendorId, ProductId productId) {
        return productRepository
                .findByIdAndVendorId(productId, vendorId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));
    }

    private ProductListResponse products(List<Product> products, long total) {
        return ProductListResponse.builder()
                .list(products.stream()
                        .map(productDataMapper::transformProductToProductResponse)
                        .toList())
                .total(total)
                .build();
    }
}
