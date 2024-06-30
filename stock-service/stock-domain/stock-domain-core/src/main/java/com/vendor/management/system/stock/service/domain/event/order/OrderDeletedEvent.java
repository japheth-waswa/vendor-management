package com.vendor.management.system.stock.service.domain.event.order;

import com.vendor.management.system.stock.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderDeletedEvent extends OrderEvent{
    public OrderDeletedEvent(Order order, ZonedDateTime createdAt) {
        super(order, createdAt);
    }
}
