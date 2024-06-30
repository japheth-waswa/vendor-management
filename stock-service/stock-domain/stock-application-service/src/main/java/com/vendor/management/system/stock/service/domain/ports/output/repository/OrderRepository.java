package com.vendor.management.system.stock.service.domain.ports.output.repository;


import com.vendor.management.system.stock.service.domain.entity.Order;
import com.vendor.management.system.stock.service.domain.valueobject.OrderSortField;
import com.vendor.management.system.stock.service.domain.valueobject.ProductId;
import com.vendor.management.system.domain.valueobject.*;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);

    Order update(Order order);

    Optional<Order> findByIdAndVendorId(OrderId orderId, VendorId vendorId);

    Optional<Order> deleteOrderItem(Order order, OrderId orderId, VendorId vendorId, OrderItemId orderItemId);

    Optional<Order> deleteOrderItem(Order order, OrderId orderId, VendorId vendorId, ProductId productId);

    void deleteOrder(OrderId orderId, VendorId vendorId);

    Optional<List<Order>> findAll(VendorId vendorId, int pageNumber, int pageSize);

    Optional<List<Order>> findAll(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, OrderSortField>> sort);

    long countAll(VendorId vendorId);
}
