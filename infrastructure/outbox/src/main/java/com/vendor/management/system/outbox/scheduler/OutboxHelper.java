package com.vendor.management.system.outbox.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendor.management.system.domain.event.payload.EventPayload;
import com.vendor.management.system.domain.exception.DomainException;
import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.outbox.model.OutboxMessage;
import com.vendor.management.system.outbox.repository.OutboxRepository;
import com.vendor.management.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class OutboxHelper<T extends OutboxRepository<U>, U extends OutboxMessage, V extends EventPayload> {
    private final T outboxRepository;
    private final ObjectMapper objectMapper;
    private final String sagaName;

    public OutboxHelper(T outboxRepository, ObjectMapper objectMapper,
                        String sagaName) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.sagaName = sagaName;
    }

    @Transactional(readOnly = true)
    public Optional<List<U>> getOrderOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                              SagaStatus... sagaStatus) {
        return outboxRepository.findByTypeAndOutboxStatusAndSagaStatus(sagaName, outboxStatus, sagaStatus);
    }

    @Transactional(readOnly = true)
    public Optional<U> getOrderOutboxMessageBySagaIdAndSagaStatus(UUID sagaId,
                                                                  SagaStatus... sagaStatus) {
        return outboxRepository.findByTypeAndSagaIdAndSagaStatus(sagaName, sagaId, sagaStatus);
    }

    @Transactional
    protected void save(U outboxMessage) {
        U response = outboxRepository.save(outboxMessage);
        if (response == null) {
            log.error("Could not save OutboxMessage with outbox id: {}", outboxMessage.getOutboxMessageId());
            throw new DomainException("Could not save OutboxMessage with outbox id: " + outboxMessage.getOutboxMessageId());
        }
        log.info("OutboxMessage saved with outbox id: {}", outboxMessage.getOutboxMessageId());
    }

    protected String createPayload(V eventPayload) {
        try {
            return objectMapper.writeValueAsString(eventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create EventPayload object for {} with id: {}",
                    eventPayload.getEventName(), eventPayload.getEventId(), e);
            throw new DomainException("Could not create EventPayload object for " +
                    eventPayload.getEventName() + " with id: " + eventPayload.getEventId(), e);
        }
    }

    @Transactional
    public void deleteOrderOutboxMessageByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus,
                                                                    SagaStatus... sagaStatus) {
        outboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(sagaName, outboxStatus, sagaStatus);
    }
}
