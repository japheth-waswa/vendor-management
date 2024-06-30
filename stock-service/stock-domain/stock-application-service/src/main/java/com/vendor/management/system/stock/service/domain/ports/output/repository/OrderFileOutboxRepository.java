package com.vendor.management.system.stock.service.domain.ports.output.repository;

import com.vendor.management.system.outbox.model.OrderOutboxMessage;
import com.vendor.management.system.outbox.repository.OutboxRepository;

public interface OrderFileOutboxRepository extends OutboxRepository<OrderOutboxMessage> {}
