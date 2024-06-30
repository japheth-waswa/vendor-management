package com.vendor.management.system.stock.service.dataaccess.productcategory.repository;

import com.vendor.management.system.stock.service.dataaccess.productcategory.entity.ProductCategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCategoryJpaRepository extends JpaRepository<ProductCategoryEntity, UUID> {
    Optional<ProductCategoryEntity> findByIdAndVendorId(UUID id, UUID vendorId);

    Page<ProductCategoryEntity> findAllByVendorId(UUID vendorId, Pageable pageable);

    void deleteByIdAndVendorId(UUID id, UUID vendorId);

    long countByVendorId(UUID vendorId);
}
