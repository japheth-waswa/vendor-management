package com.vendor.management.system.stock.service.dataaccess.outbox.common.mapper;

import com.vendor.management.system.outbox.model.OrderOutboxMessage;
import com.vendor.management.system.stock.service.dataaccess.outbox.file.entity.OrderFileOutboxEntity;
import com.vendor.management.system.stock.service.dataaccess.outbox.finance.entity.OrderFinanceOutboxEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxDataAccessMapper {

    public OrderFileOutboxEntity orderOutboxMessageToOrderFileOutboxEntity(OrderOutboxMessage orderOutboxMessage) {
        return OrderFileOutboxEntity.builder()
                .id(orderOutboxMessage.getId())
                .sagaId(orderOutboxMessage.getSagaId())
                .createdAt(orderOutboxMessage.getCreatedAt())
                .type(orderOutboxMessage.getType())
                .payload(orderOutboxMessage.getPayload())
                .orderStatus(orderOutboxMessage.getOrderStatus())
                .sagaStatus(orderOutboxMessage.getSagaStatus())
                .outboxStatus(orderOutboxMessage.getOutboxStatus())
                .version(orderOutboxMessage.getVersion())
                .build();
    }

    public OrderOutboxMessage orderFileOutboxEntityToOrderOutboxMessage(OrderFileOutboxEntity orderFileOutboxEntity) {
        return OrderOutboxMessage.builder()
                .id(orderFileOutboxEntity.getId())
                .sagaId(orderFileOutboxEntity.getSagaId())
                .createdAt(orderFileOutboxEntity.getCreatedAt())
                .type(orderFileOutboxEntity.getType())
                .payload(orderFileOutboxEntity.getPayload())
                .orderStatus(orderFileOutboxEntity.getOrderStatus())
                .sagaStatus(orderFileOutboxEntity.getSagaStatus())
                .outboxStatus(orderFileOutboxEntity.getOutboxStatus())
                .version(orderFileOutboxEntity.getVersion())
                .build();
    }

    public OrderFinanceOutboxEntity orderOutboxMessageToOrderFinanceOutboxEntity(OrderOutboxMessage orderOutboxMessage) {
        return OrderFinanceOutboxEntity.builder()
                .id(orderOutboxMessage.getId())
                .sagaId(orderOutboxMessage.getSagaId())
                .createdAt(orderOutboxMessage.getCreatedAt())
                .type(orderOutboxMessage.getType())
                .payload(orderOutboxMessage.getPayload())
                .orderStatus(orderOutboxMessage.getOrderStatus())
                .sagaStatus(orderOutboxMessage.getSagaStatus())
                .outboxStatus(orderOutboxMessage.getOutboxStatus())
                .version(orderOutboxMessage.getVersion())
                .build();
    }

    public OrderOutboxMessage orderFinanceOutboxEntityToOrderOutboxMessage(OrderFinanceOutboxEntity orderFinanceOutboxEntity) {
        return OrderOutboxMessage.builder()
                .id(orderFinanceOutboxEntity.getId())
                .sagaId(orderFinanceOutboxEntity.getSagaId())
                .createdAt(orderFinanceOutboxEntity.getCreatedAt())
                .type(orderFinanceOutboxEntity.getType())
                .payload(orderFinanceOutboxEntity.getPayload())
                .orderStatus(orderFinanceOutboxEntity.getOrderStatus())
                .sagaStatus(orderFinanceOutboxEntity.getSagaStatus())
                .outboxStatus(orderFinanceOutboxEntity.getOutboxStatus())
                .version(orderFinanceOutboxEntity.getVersion())
                .build();
    }
}
