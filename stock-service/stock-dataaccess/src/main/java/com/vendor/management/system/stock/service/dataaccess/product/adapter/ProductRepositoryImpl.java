package com.vendor.management.system.stock.service.dataaccess.product.adapter;

import com.vendor.management.system.dataaccess.util.DataAccessHelper;
import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.dataaccess.product.entity.ProductEntity;
import com.vendor.management.system.stock.service.dataaccess.product.mapper.ProductDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.product.repository.ProductJpaRepository;
import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.ports.output.repository.ProductRepository;
import com.vendor.management.system.stock.service.domain.valueobject.ProductId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductName;
import com.vendor.management.system.stock.service.domain.valueobject.ProductSortField;
import com.vendor.management.system.stock.service.domain.valueobject.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final ProductDataAccessMapper productDataAccessMapper;
    private final DataAccessHelper dataAccessHelper;

    public ProductRepositoryImpl(ProductJpaRepository productJpaRepository,
                                 ProductDataAccessMapper productDataAccessMapper,
                                 DataAccessHelper dataAccessHelper) {
        this.productJpaRepository = productJpaRepository;
        this.productDataAccessMapper = productDataAccessMapper;
        this.dataAccessHelper = dataAccessHelper;
    }

    @Override
    public Product save(Product product) {
        return productDataAccessMapper.productEntityToProduct(
                productJpaRepository.save(productDataAccessMapper.productToProductEntity(product)));
    }

    @Override
    public Product update(Product product) {
        return productDataAccessMapper.productEntityToProduct(
                productJpaRepository.save(productDataAccessMapper.productToProductEntityUpdate(product)));
    }

    @Override
    @Transactional
    public void delete(ProductId productId, VendorId vendorId) {
        productJpaRepository.deleteByIdAndVendorId(productId.getValue(), vendorId.getValue());
    }

    @Override
    public Optional<Product> findByIdAndVendorId(ProductId productId, VendorId vendorId) {
        return productJpaRepository.findByIdAndVendorId(productId.getValue(), vendorId.getValue())
                .map(productDataAccessMapper::productEntityToProduct);
    }

    @Override
    public Optional<List<Product>> findAll(VendorId vendorId, int pageNumber, int pageSize) {
        return findAllRecords(vendorId, pageNumber, pageSize, null);
    }

    @Override
    public Optional<List<Product>> findAll(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductSortField>> sort) {
        if (sort == null || sort.isEmpty()) {
            return findAll(vendorId, pageNumber, pageSize);
        }
        List<Sort.Order> orders = getSortOrders(sort);
        return findAllRecords(vendorId, pageNumber, pageSize, orders);
    }

    @Override
    public Optional<List<Product>> findAllByName(VendorId vendorId, ProductName productName, ProductStatus productStatus, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductSortField>> sort) {
        if (sort == null || sort.isEmpty()) {
            return findAllRecords(vendorId, productName, productStatus, pageNumber, pageSize, null);
        }
        List<Sort.Order> orders = getSortOrders(sort);
        return findAllRecords(vendorId, productName, productStatus, pageNumber, pageSize, orders);
    }

    private List<Sort.Order> getSortOrders(List<AbstractSortPayload<SortDirection, ProductSortField>> sort) {
        return sort.stream()
                .filter(payload -> payload.getSortDirection() != null && payload.getSortField() != null)
                .map(payload -> new Sort.Order(dataAccessHelper.parseSortDirection(payload.getSortDirection()),
                        productDataAccessMapper.productSortFieldToProductEntitySortField(payload.getSortField())))
                .toList();
    }

    @Override
    public long countAll(VendorId vendorId) {
        return productJpaRepository.countByVendorId(vendorId.getValue());
    }

    @Override
    public long countAllByName(VendorId vendorId, ProductName productName) {
        return productJpaRepository.countByVendorIdAndName(vendorId.getValue(), productName.getValue());
    }

    private Optional<List<Product>> findAllRecords(VendorId vendorId, int pageNumber, int pageSize, List<Sort.Order> orders) {
        Page<ProductEntity> products = productJpaRepository.findAllByVendorId(vendorId.getValue(),
                dataAccessHelper.buildPageable(pageNumber, pageSize, orders != null ? orders : Collections.emptyList()));
        if (!products.hasContent()) {
            return Optional.empty();
        }
        return Optional.of(products.stream()
                .map(productDataAccessMapper::productEntityToProduct)
                .toList());
    }

    private Optional<List<Product>> findAllRecords(VendorId vendorId, ProductName productName, ProductStatus productStatus, int pageNumber, int pageSize, List<Sort.Order> orders) {
        Page<ProductEntity> products = productJpaRepository.findAllByVendorIdAndNameAndProductStatus(vendorId.getValue(), productName.getValue(), productStatus,
                dataAccessHelper.buildPageable(pageNumber, pageSize, orders != null ? orders : Collections.emptyList()));
        if (!products.hasContent()) {
            return Optional.empty();
        }
        return Optional.of(products.stream()
                .map(productDataAccessMapper::productEntityToProduct)
                .toList());
    }
}
