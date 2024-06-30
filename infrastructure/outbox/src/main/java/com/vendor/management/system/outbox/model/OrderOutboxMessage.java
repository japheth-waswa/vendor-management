package com.vendor.management.system.outbox.model;

import com.vendor.management.system.domain.valueobject.OrderStatus;
import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderOutboxMessage extends OutboxMessage{
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    @Setter
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    @Setter
    private SagaStatus sagaStatus;
    @Setter
    private OrderStatus orderStatus;
    @Setter
    private OutboxStatus outboxStatus;
    private int version;

    @Override
    public UUID getOutboxMessageId() {
        return id;
    }
}
