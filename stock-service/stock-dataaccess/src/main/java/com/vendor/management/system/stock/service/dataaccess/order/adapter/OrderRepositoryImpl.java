package com.vendor.management.system.stock.service.dataaccess.order.adapter;

import com.vendor.management.system.stock.service.dataaccess.order.entity.OrderEntity;
import com.vendor.management.system.stock.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.vendor.management.system.stock.service.dataaccess.order.repository.OrderItemJpaRepository;
import com.vendor.management.system.stock.service.dataaccess.order.repository.OrderJpaRepository;
import com.vendor.management.system.stock.service.domain.entity.Order;
import com.vendor.management.system.stock.service.domain.valueobject.OrderSortField;
import com.vendor.management.system.stock.service.domain.valueobject.ProductId;
import com.vendor.management.system.dataaccess.util.DataAccessHelper;
import com.vendor.management.system.domain.valueobject.*;
import com.vendor.management.system.stock.service.domain.ports.output.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;
    private final DataAccessHelper dataAccessHelper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository,
                               OrderItemJpaRepository orderItemJpaRepository,
                               OrderDataAccessMapper orderDataAccessMapper,
                               DataAccessHelper dataAccessHelper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderItemJpaRepository = orderItemJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
        this.dataAccessHelper = dataAccessHelper;
    }

    @Override
    public Order save(Order order) {
        return orderDataAccessMapper.orderEntityToOrder(
                orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntityCreate(order)));
    }

    @Override
    public Order update(Order order) {
        OrderEntity orderEntity = orderDataAccessMapper.orderToOrderEntityUpdate(order);
        orderItemJpaRepository.deleteAllByOrderEntityId(orderEntity.getId());//todo this deleting and recreating the order items is in-efficient. Find a better way to update order items without deleting and recreating them
        return orderDataAccessMapper.orderEntityToOrder(
                orderJpaRepository.save(orderEntity));
    }

    @Transactional
    @Override
    public Optional<Order> findByIdAndVendorId(OrderId orderId, VendorId vendorId) {
        return orderJpaRepository.findByIdAndVendorId(orderId.getValue(), vendorId.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> deleteOrderItem(Order order, OrderId orderId, VendorId vendorId, OrderItemId orderItemId) {
//        return Optional.of(orderDataAccessMapper.orderEntityToOrder(
//                orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntityUpdate(order))));
        return Optional.of(update(order));
    }

    @Override
    public Optional<Order> deleteOrderItem(Order order, OrderId orderId, VendorId vendorId, ProductId productId) {
//        return Optional.of(orderDataAccessMapper.orderEntityToOrder(
//                orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntityUpdate(order))));
        return Optional.of(update(order));
    }

    @Override
    @Transactional
    public void deleteOrder(OrderId orderId, VendorId vendorId) {
        orderJpaRepository.deleteByIdAndVendorId(orderId.getValue(), vendorId.getValue());
    }

    @Transactional
    @Override
    public Optional<List<Order>> findAll(VendorId vendorId, int pageNumber, int pageSize) {
        return findAllRecords(vendorId, pageNumber, pageSize, null);
    }

    @Transactional
    @Override
    public Optional<List<Order>> findAll(VendorId vendorId, int pageNumber, int pageSize,
                                         List<AbstractSortPayload<SortDirection, OrderSortField>> sort) {
        if (sort == null || sort.isEmpty()) {
            return findAll(vendorId, pageNumber, pageSize);
        }
        List<Sort.Order> orders = sort.stream()
                .filter(payload -> payload.getSortDirection() != null && payload.getSortField() != null)
                .map(payload -> new Sort.Order(dataAccessHelper.parseSortDirection(payload.getSortDirection()),
                        orderDataAccessMapper.orderSortFieldToOrderEntitySortField(payload.getSortField())))
                .toList();
        return findAllRecords(vendorId, pageNumber, pageSize, orders);
    }

    @Override
    public long countAll(VendorId vendorId) {
        return orderJpaRepository.countByVendorId(vendorId.getValue());
    }

    private Optional<List<Order>> findAllRecords(VendorId vendorId, int pageNumber, int pageSize,
                                                 List<Sort.Order> orders) {
        Page<OrderEntity> ordersList = orderJpaRepository.findAllByVendorId(vendorId.getValue(),
                dataAccessHelper.buildPageable(pageNumber, pageSize, orders != null ? orders : Collections.emptyList()));
        if (!ordersList.hasContent()) {
            return Optional.empty();
        }
        return Optional.of(ordersList.stream()
                .map(orderDataAccessMapper::orderEntityToOrder)
                .toList());
    }
}
