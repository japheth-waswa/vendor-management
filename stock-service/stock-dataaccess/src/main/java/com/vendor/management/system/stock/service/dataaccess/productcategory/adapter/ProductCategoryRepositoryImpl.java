package com.vendor.management.system.stock.service.dataaccess.productcategory.adapter;

import com.vendor.management.system.dataaccess.util.DataAccessHelper;
import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.dataaccess.productcategory.entity.ProductCategoryEntity;
import com.vendor.management.system.stock.service.dataaccess.productcategory.mapper.ProductCategoryDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.productcategory.repository.ProductCategoryJpaRepository;
import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.stock.service.domain.ports.output.repository.ProductCategoryRepository;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategorySortField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class ProductCategoryRepositoryImpl implements ProductCategoryRepository {
    private final ProductCategoryJpaRepository productCategoryJpaRepository;
    private final ProductCategoryDataAccessMapper productCategoryDataAccessMapper;
    private final DataAccessHelper dataAccessHelper;

    public ProductCategoryRepositoryImpl(ProductCategoryJpaRepository productCategoryJpaRepository,
                                         ProductCategoryDataAccessMapper productCategoryDataAccessMapper,
                                         DataAccessHelper dataAccessHelper) {
        this.productCategoryJpaRepository = productCategoryJpaRepository;
        this.productCategoryDataAccessMapper = productCategoryDataAccessMapper;
        this.dataAccessHelper = dataAccessHelper;
    }

    @Override
    public ProductCategory save(ProductCategory productCategory) {
            return productCategoryDataAccessMapper.productCategoryEntityToProductCategory(
                    productCategoryJpaRepository.save(productCategoryDataAccessMapper.productCategoryToProductCategoryEntity(productCategory)));
    }

    @Override
    public ProductCategory update(ProductCategory productCategory) {
        return productCategoryDataAccessMapper.productCategoryEntityToProductCategory(
                productCategoryJpaRepository.save(productCategoryDataAccessMapper.productCategoryToProductCategoryEntityUpdate(productCategory)));
    }

    @Override
    @Transactional
    public void delete(ProductCategoryId productCategoryId, VendorId vendorId) {
        productCategoryJpaRepository.deleteByIdAndVendorId(productCategoryId.getValue(), vendorId.getValue());
    }

    @Override
    public Optional<ProductCategory> findByIdAndVendorId(ProductCategoryId productCategoryId, VendorId vendorId) {
        return productCategoryJpaRepository.findByIdAndVendorId(productCategoryId.getValue(), vendorId.getValue())
                .map(productCategoryDataAccessMapper::productCategoryEntityToProductCategory);
    }

    @Override
    public Optional<List<ProductCategory>> findAll(VendorId vendorId, int pageNumber, int pageSize) {
        return findAllRecords(vendorId, pageNumber, pageSize, null);
    }

    @Override
    public Optional<List<ProductCategory>> findAll(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductCategorySortField>> sort) {
        if (sort.isEmpty()) {
            return findAll(vendorId, pageNumber, pageSize);
        }
        List<Sort.Order> orders = sort.stream()
                .filter(payload -> payload.getSortDirection() != null && payload.getSortField() != null)
                .map(payload -> new Sort.Order(dataAccessHelper.parseSortDirection(payload.getSortDirection()),
                        productCategoryDataAccessMapper.productCategorySortFieldToProductCategoryEntitySortField(payload.getSortField())))
                .toList();
        return findAllRecords(vendorId, pageNumber, pageSize, orders);
    }

    @Override
    public long countAll(VendorId vendorId) {
        return productCategoryJpaRepository.countByVendorId(vendorId.getValue());
    }

    private Optional<List<ProductCategory>> findAllRecords(VendorId vendorId, int pageNumber, int pageSize, List<Sort.Order> orders) {
        Page<ProductCategoryEntity> productCategories = productCategoryJpaRepository.findAllByVendorId(vendorId.getValue(), dataAccessHelper.buildPageable(pageNumber, pageSize, orders != null ? orders : Collections.emptyList()));
        if (!productCategories.hasContent()) {
            return Optional.empty();
        }
        return Optional.of(productCategories.stream()
                .map(productCategoryDataAccessMapper::productCategoryEntityToProductCategory)
                .toList());
    }
}
