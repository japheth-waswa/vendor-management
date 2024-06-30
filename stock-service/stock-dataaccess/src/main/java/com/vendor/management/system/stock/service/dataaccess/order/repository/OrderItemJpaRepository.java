package com.vendor.management.system.stock.service.dataaccess.order.repository;

import com.vendor.management.system.stock.service.dataaccess.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItemEntity, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM OrderItemEntity oi WHERE oi.order.id = :orderId")
    void deleteAllByOrderEntityId(@Param("orderId") UUID orderId);
}
