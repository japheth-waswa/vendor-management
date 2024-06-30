package com.vendor.management.system.stock.service.dataaccess.outbox.file.adapter;

import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.outbox.model.OrderOutboxMessage;
import com.vendor.management.system.saga.SagaStatus;
import com.vendor.management.system.stock.service.dataaccess.outbox.common.mapper.OrderOutboxDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.outbox.file.repository.OrderFileOutboxJpaRepository;
import com.vendor.management.system.stock.service.dataaccess.outbox.common.exception.OrderOutboxNotFound;
import com.vendor.management.system.stock.service.domain.ports.output.repository.OrderFileOutboxRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderFileOutboxRepositoryImpl implements OrderFileOutboxRepository {
    private final OrderFileOutboxJpaRepository orderFileOutboxJpaRepository;
    private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

    public OrderFileOutboxRepositoryImpl(OrderFileOutboxJpaRepository orderFileOutboxJpaRepository,
                                         OrderOutboxDataAccessMapper orderOutboxDataAccessMapper) {
        this.orderFileOutboxJpaRepository = orderFileOutboxJpaRepository;
        this.orderOutboxDataAccessMapper = orderOutboxDataAccessMapper;
    }

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage outboxMessage) {
        return orderOutboxDataAccessMapper
                .orderFileOutboxEntityToOrderOutboxMessage(orderFileOutboxJpaRepository
                        .save(orderOutboxDataAccessMapper.orderOutboxMessageToOrderFileOutboxEntity(outboxMessage)));
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return Optional.of(orderFileOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(sagaType,
                        outboxStatus, Arrays.asList(sagaStatus))
                .orElseThrow(() -> new OrderOutboxNotFound("Order file outbox object could not be found for saga type " + sagaType))
                .stream()
                .map(orderOutboxDataAccessMapper::orderFileOutboxEntityToOrderOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String sagaType, UUID sagaId, SagaStatus... sagaStatus) {
        return orderFileOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(sagaType, sagaId, Arrays.asList(sagaStatus))
                .map(orderOutboxDataAccessMapper::orderFileOutboxEntityToOrderOutboxMessage);
    }

    @Override
    @Transactional
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        orderFileOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, Arrays.asList(sagaStatus));
    }
}
