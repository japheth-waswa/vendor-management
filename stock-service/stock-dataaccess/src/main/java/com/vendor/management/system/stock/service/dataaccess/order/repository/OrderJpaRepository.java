package com.vendor.management.system.stock.service.dataaccess.order.repository;

import com.vendor.management.system.stock.service.dataaccess.order.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByIdAndVendorId(UUID id, UUID vendorId);

    Page<OrderEntity> findAllByVendorId(UUID vendorId, Pageable pageable);

    @Transactional
    void deleteByIdAndVendorId(UUID id, UUID vendorId);

    long countByVendorId(UUID vendorId);
}
