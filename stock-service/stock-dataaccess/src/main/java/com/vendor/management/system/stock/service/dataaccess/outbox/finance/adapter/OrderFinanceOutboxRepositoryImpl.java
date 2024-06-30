package com.vendor.management.system.stock.service.dataaccess.outbox.finance.adapter;

import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.outbox.model.OrderOutboxMessage;
import com.vendor.management.system.saga.SagaStatus;
import com.vendor.management.system.stock.service.dataaccess.outbox.common.exception.OrderOutboxNotFound;
import com.vendor.management.system.stock.service.dataaccess.outbox.common.mapper.OrderOutboxDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.outbox.finance.repository.OrderFinanceOutboxJpaRepository;
import com.vendor.management.system.stock.service.domain.ports.output.repository.OrderFinanceOutboxRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderFinanceOutboxRepositoryImpl implements OrderFinanceOutboxRepository {
    private final OrderFinanceOutboxJpaRepository orderFinanceOutboxJpaRepository;
    private final OrderOutboxDataAccessMapper orderOutboxDataAccessMapper;

    public OrderFinanceOutboxRepositoryImpl(
            OrderFinanceOutboxJpaRepository orderFinanceOutboxJpaRepository,
            OrderOutboxDataAccessMapper orderOutboxDataAccessMapper) {
        this.orderFinanceOutboxJpaRepository = orderFinanceOutboxJpaRepository;
        this.orderOutboxDataAccessMapper = orderOutboxDataAccessMapper;
    }

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage outboxMessage) {
        return orderOutboxDataAccessMapper
                .orderFinanceOutboxEntityToOrderOutboxMessage(orderFinanceOutboxJpaRepository
                        .save(orderOutboxDataAccessMapper.orderOutboxMessageToOrderFinanceOutboxEntity(outboxMessage)));
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return Optional.of(orderFinanceOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(sagaType,
                        outboxStatus, Arrays.asList(sagaStatus))
                .orElseThrow(() -> new OrderOutboxNotFound("Order finance outbox object could not be found for saga type " + sagaType))
                .stream()
                .map(orderOutboxDataAccessMapper::orderFinanceOutboxEntityToOrderOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndSagaStatus(String sagaType, UUID sagaId, SagaStatus... sagaStatus) {
        return orderFinanceOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(sagaType, sagaId, Arrays.asList(sagaStatus))
                .map(orderOutboxDataAccessMapper::orderFinanceOutboxEntityToOrderOutboxMessage);
    }

    @Override
    @Transactional
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String sagaType, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        orderFinanceOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, Arrays.asList(sagaStatus));
    }
}
