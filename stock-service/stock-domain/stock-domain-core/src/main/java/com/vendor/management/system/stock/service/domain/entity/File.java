package com.vendor.management.system.stock.service.domain.entity;

import com.vendor.management.system.domain.entity.BaseEntity;
import com.vendor.management.system.domain.valueobject.FileId;
import com.vendor.management.system.domain.valueobject.OrderId;

public class File extends BaseEntity<FileId> {
    private final OrderId orderId;

    public File(OrderId orderId) {
        this.orderId = orderId;
    }

    public OrderId getOrderId() {
        return orderId;
    }
}
