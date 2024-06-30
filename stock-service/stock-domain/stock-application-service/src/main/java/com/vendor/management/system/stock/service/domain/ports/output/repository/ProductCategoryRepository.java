package com.vendor.management.system.stock.service.domain.ports.output.repository;

import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategoryId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductCategorySortField;
import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository {

    ProductCategory save(ProductCategory productCategory);

    ProductCategory update(ProductCategory productCategory);

    void delete(ProductCategoryId productCategoryId, VendorId vendorId);

    Optional<ProductCategory> findByIdAndVendorId(ProductCategoryId productCategoryId, VendorId vendorId);

    Optional<List<ProductCategory>> findAll(VendorId vendorId, int pageNumber, int pageSize);

    Optional<List<ProductCategory>> findAll(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductCategorySortField>> sort);

    long countAll(VendorId vendorId);

}
