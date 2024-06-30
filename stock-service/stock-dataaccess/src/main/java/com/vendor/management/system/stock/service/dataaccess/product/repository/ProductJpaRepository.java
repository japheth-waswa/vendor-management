package com.vendor.management.system.stock.service.dataaccess.product.repository;

import com.vendor.management.system.stock.service.dataaccess.product.entity.ProductEntity;
import com.vendor.management.system.stock.service.domain.valueobject.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findByIdAndVendorId(UUID id, UUID vendorId);

    Page<ProductEntity> findAllByVendorId(UUID vendorId, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.vendorId = :vendorId AND p.productStatus = :productStatus AND p.name LIKE %:name%")
    Page<ProductEntity> findAllByVendorIdAndNameAndProductStatus(UUID vendorId, String name, ProductStatus productStatus, Pageable pageable);

    void deleteByIdAndVendorId(UUID id, UUID vendorId);

    long countByVendorId(UUID vendorId);

    @Query("SELECT count(p) FROM ProductEntity p WHERE p.vendorId = :vendorId AND p.name LIKE %:name%")
    long countByVendorIdAndName(UUID vendorId, String name);
}
