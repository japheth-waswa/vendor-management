package com.vendor.management.system.stock.service.dataaccess.outbox.file.repository;

import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.saga.SagaStatus;
import com.vendor.management.system.stock.service.dataaccess.outbox.file.entity.OrderFileOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderFileOutboxJpaRepository extends JpaRepository<OrderFileOutboxEntity, UUID> {
    Optional<List<OrderFileOutboxEntity>> findByTypeAndOutboxStatusAndSagaStatusIn(String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatus);

    Optional<OrderFileOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(String type, UUID sagaId, List<SagaStatus> sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatusIn(String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatus);
}
