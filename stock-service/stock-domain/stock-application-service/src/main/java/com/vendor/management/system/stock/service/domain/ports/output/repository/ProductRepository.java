package com.vendor.management.system.stock.service.domain.ports.output.repository;

import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.valueobject.ProductId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductName;
import com.vendor.management.system.stock.service.domain.valueobject.ProductSortField;
import com.vendor.management.system.stock.service.domain.valueobject.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Product update(Product product);

    void delete(ProductId productId, VendorId vendorId);

    Optional<Product> findByIdAndVendorId(ProductId productId, VendorId vendorId);

    Optional<List<Product>> findAll(VendorId vendorId, int pageNumber, int pageSize);

    Optional<List<Product>> findAll(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductSortField>> sort);

    Optional<List<Product>> findAllByName(VendorId vendorId, ProductName productName, ProductStatus productStatus, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductSortField>> sort);

    long countAll(VendorId vendorId);

    long countAllByName(VendorId vendorId, ProductName productName);
}
