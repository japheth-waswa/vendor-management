package com.vendor.management.system.stock.service.domain.outbox.scheduler.finance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendor.management.system.domain.event.payload.OrderEventPayload;
import com.vendor.management.system.domain.valueobject.OrderStatus;
import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.outbox.model.OrderOutboxMessage;
import com.vendor.management.system.outbox.scheduler.OutboxHelper;
import com.vendor.management.system.saga.SagaStatus;
import com.vendor.management.system.stock.service.domain.event.order.OrderDeletedEvent;
import com.vendor.management.system.stock.service.domain.event.order.OrderUpdateEvent;
import com.vendor.management.system.stock.service.domain.ports.output.repository.OrderFinanceOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.vendor.management.system.domain.util.DomainConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
public class FinanceOutboxHelper extends OutboxHelper<OrderFinanceOutboxRepository,OrderOutboxMessage,OrderEventPayload> {
    public FinanceOutboxHelper(OrderFinanceOutboxRepository outboxRepository, ObjectMapper objectMapper) {
        super(outboxRepository, objectMapper, ORDER_SAGA_NAME);
    }

    @Transactional
    public void saveOutboxMessage(OrderEventPayload eventPayload, OrderStatus orderStatus, SagaStatus sagaStatus, OutboxStatus outboxStatus, UUID sagaId) {
        save(OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .createdAt(eventPayload.getCreatedAt())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(eventPayload))
                .orderStatus(orderStatus)
                .sagaStatus(sagaStatus)
                .outboxStatus(outboxStatus)
                .build());
    }


    public OrderEventPayload transformOrderUpdatedEventToOrderEventPayload(OrderUpdateEvent orderUpdateEvent) {
        return OrderEventPayload.builder()
                .orderId(orderUpdateEvent.getOrder().getId().getValue().toString())
                .customerId(orderUpdateEvent.getOrder().getCustomerId().getValue().toString())
                .price(orderUpdateEvent.getOrder().getPrice().getAmount())
                .createdAt(orderUpdateEvent.getCreatedAt())
                .orderStatus(orderUpdateEvent.getOrder().getOrderStatus().name())
                .build();
    }

    public OrderEventPayload transformOrderDeletedEventToOrderStatusEventPayload(OrderDeletedEvent orderDeletedEvent) {
        return OrderEventPayload.builder()
                .orderId(orderDeletedEvent.getOrder().getId().getValue().toString())
                .customerId(orderDeletedEvent.getOrder().getCustomerId().getValue().toString())
                .price(orderDeletedEvent.getOrder().getPrice().getAmount())
                .createdAt(orderDeletedEvent.getCreatedAt())
                .orderStatus(orderDeletedEvent.getOrder().getOrderStatus().name())
                .build();
    }

    public SagaStatus orderStatusToSagaStatus(OrderStatus orderStatus) {
        return switch (orderStatus) {
            case SETTLED, CANCELLED -> SagaStatus.PROCESSING;
            default -> SagaStatus.STARTED;
        };
    }
}
