package com.vendor.management.system.outbox.repository;

import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.outbox.model.OutboxMessage;
import com.vendor.management.system.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxRepository<T extends OutboxMessage> {
    T save(T outboxMessage);

    Optional<List<T>> findByTypeAndOutboxStatusAndSagaStatus(String sagaType,
                                                                              OutboxStatus outboxStatus,
                                                                              SagaStatus... sagaStatus);

    Optional<T> findByTypeAndSagaIdAndSagaStatus(String sagaType,
                                                                  UUID sagaId,
                                                                  SagaStatus... sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatus(String sagaType,
                                                  OutboxStatus outboxStatus,
                                                  SagaStatus... sagaStatus);
}
