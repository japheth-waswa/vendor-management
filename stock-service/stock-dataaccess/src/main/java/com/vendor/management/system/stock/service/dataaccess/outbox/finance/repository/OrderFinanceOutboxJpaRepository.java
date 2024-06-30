package com.vendor.management.system.stock.service.dataaccess.outbox.finance.repository;

import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.saga.SagaStatus;
import com.vendor.management.system.stock.service.dataaccess.outbox.finance.entity.OrderFinanceOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderFinanceOutboxJpaRepository extends JpaRepository<OrderFinanceOutboxEntity, UUID> {
    Optional<List<OrderFinanceOutboxEntity>> findByTypeAndOutboxStatusAndSagaStatusIn(String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatus);

    Optional<OrderFinanceOutboxEntity> findByTypeAndSagaIdAndSagaStatusIn(String type, UUID sagaId, List<SagaStatus> sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatusIn(String type, OutboxStatus outboxStatus, List<SagaStatus> sagaStatus);
}
