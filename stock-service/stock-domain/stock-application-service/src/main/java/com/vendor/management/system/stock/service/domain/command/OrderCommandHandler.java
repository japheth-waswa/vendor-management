package com.vendor.management.system.stock.service.domain.command;

import com.vendor.management.system.domain.valueobject.*;
import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.stock.service.domain.StockDomainService;
import com.vendor.management.system.stock.service.domain.dto.order.*;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderListResponse;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderResponse;
import com.vendor.management.system.stock.service.domain.entity.Order;
import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.event.order.OrderDeletedEvent;
import com.vendor.management.system.stock.service.domain.event.order.OrderUpdateEvent;
import com.vendor.management.system.stock.service.domain.exception.OrderNotFoundException;
import com.vendor.management.system.stock.service.domain.exception.ProductNotFoundException;
import com.vendor.management.system.stock.service.domain.mapper.OrderDataMapper;
import com.vendor.management.system.stock.service.domain.outbox.scheduler.file.FileOutboxHelper;
import com.vendor.management.system.stock.service.domain.outbox.scheduler.finance.FinanceOutboxHelper;
import com.vendor.management.system.stock.service.domain.ports.output.repository.OrderRepository;
import com.vendor.management.system.stock.service.domain.ports.output.repository.ProductRepository;
import com.vendor.management.system.stock.service.domain.valueobject.OrderSortField;
import com.vendor.management.system.stock.service.domain.valueobject.ProductId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.vendor.management.system.domain.util.DomainConstants.Order_Not_Found_Exception_Message;
import static com.vendor.management.system.domain.util.DomainConstants.Orders_Not_Found_Exception_Message;

@Slf4j
@Component
public class OrderCommandHandler {
    private final StockDomainService stockDomainService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDataMapper orderDataMapper;
    private final FileOutboxHelper fileOutboxHelper;
    private final FinanceOutboxHelper financeOutboxHelper;

    public OrderCommandHandler(StockDomainService stockDomainService,
                               OrderRepository orderRepository,
                               ProductRepository productRepository,
                               OrderDataMapper orderDataMapper,
                               FileOutboxHelper fileOutboxHelper,
                               FinanceOutboxHelper financeOutboxHelper) {
        this.stockDomainService = stockDomainService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderDataMapper = orderDataMapper;
        this.fileOutboxHelper = fileOutboxHelper;
        this.financeOutboxHelper = financeOutboxHelper;
    }

    public OrderResponse fetchOrder(VendorId vendorId, OrderId orderId) {
        return orderDataMapper.transformOrderToOrderResponse(orderRepository.findByIdAndVendorId(orderId, vendorId)
                .orElseThrow(() -> new OrderNotFoundException(Order_Not_Found_Exception_Message)));
    }

    @Transactional
    public OrderResponse createOrder(VendorId vendorId, CreateOrderCommand createOrderCommand) {
        Order order = orderDataMapper.transformCreateOrderCommandToOrder(vendorId, createOrderCommand);
        stockDomainService.createOrder(order);
        return orderDataMapper.transformOrderToOrderResponse(orderRepository.save(order));
    }

    public OrderResponse modifyOrder(VendorId vendorId, ModifyOrderCommand modifyOrderCommand) {
        Product product = getExistingProduct(new ProductId(modifyOrderCommand.getProductId()), vendorId);
        Order order = getExistingOrder(new OrderId(modifyOrderCommand.getOrderId()), vendorId);
        stockDomainService.modifyOrderItem(order, product, modifyOrderCommand.getQuantity());
        return orderDataMapper.transformOrderToOrderResponse(orderRepository.update(order));
    }

    public OrderResponse deleteOrderItem(VendorId vendorId, DeleteOrderProductCommand deleteOrderProductCommand) {
        Product product = getExistingProduct(new ProductId(deleteOrderProductCommand.getProductId()), vendorId);
        Order order = getExistingOrder(new OrderId(deleteOrderProductCommand.getOrderId()), vendorId);
        stockDomainService.deleteOrderItem(order, product);
        return orderDataMapper.transformOrderToOrderResponse(orderRepository.deleteOrderItem(order, order.getId(), vendorId, product.getId()).
                orElseThrow(() -> new OrderNotFoundException(Order_Not_Found_Exception_Message)));
    }

    public OrderResponse deleteOrderItem(VendorId vendorId, DeleteOrderItemCommand deleteOrderItemCommand) {
        Order order = getExistingOrder(new OrderId(deleteOrderItemCommand.getOrderId()), vendorId);
        OrderItemId orderItemId = new OrderItemId(deleteOrderItemCommand.getOrderItemId());
        stockDomainService.deleteOrderItem(order, orderItemId);
        return orderDataMapper.transformOrderToOrderResponse(orderRepository.deleteOrderItem(order, order.getId(), vendorId, orderItemId).
                orElseThrow(() -> new OrderNotFoundException(Order_Not_Found_Exception_Message)));
    }

    @Transactional
    public OrderResponse settleOrder(VendorId vendorId, SettleOrderCommand settleOrderCommand) {
        Order order = getExistingOrder(new OrderId(settleOrderCommand.getOrderId()), vendorId);
        log.info("Settling order with id: {}", order.getId().getValue());
        OrderUpdateEvent orderUpdateEvent = stockDomainService.settleOrder(order);
        Order updatedOrder = orderRepository.update(order);

        fileOutboxHelper.saveOutboxMessage(
                fileOutboxHelper.transformOrderUpdatedEventToOrderEventPayload(orderUpdateEvent),
                orderUpdateEvent.getOrder().getOrderStatus(),
                fileOutboxHelper.orderStatusToSagaStatus(orderUpdateEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID()
        );

        financeOutboxHelper.saveOutboxMessage(
                financeOutboxHelper.transformOrderUpdatedEventToOrderEventPayload(orderUpdateEvent),
                orderUpdateEvent.getOrder().getOrderStatus(),
                financeOutboxHelper.orderStatusToSagaStatus(orderUpdateEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID()
        );

        log.info("Settled order with id: {}", updatedOrder.getId().getValue());
        return orderDataMapper.transformOrderToOrderResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse cancelOrder(VendorId vendorId, CancelOrderCommand cancelOrderCommand) {
        Order order = getExistingOrder(new OrderId(cancelOrderCommand.getOrderId()), vendorId);
        log.info("Cancelling order with id: {}", order.getId().getValue());
        OrderUpdateEvent orderUpdateEvent = stockDomainService.cancelOrder(order, cancelOrderCommand.getMessages());
        Order updatedOrder = orderRepository.update(order);

        financeOutboxHelper.saveOutboxMessage(
                financeOutboxHelper.transformOrderUpdatedEventToOrderEventPayload(orderUpdateEvent),
                orderUpdateEvent.getOrder().getOrderStatus(),
                financeOutboxHelper.orderStatusToSagaStatus(orderUpdateEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID()
        );

        log.info("Cancelled order with id: {}", order.getId().getValue());
        return orderDataMapper.transformOrderToOrderResponse(updatedOrder);
    }

    @Transactional
    public void deleteOrder(VendorId vendorId, DeleteOrderCommand deleteOrderCommand) {
        OrderId orderId = new OrderId(deleteOrderCommand.getOrderId());
        Order order = getExistingOrder(orderId, vendorId);
        log.info("Deleting order with id: {}", order.getId().getValue());
        OrderDeletedEvent orderDeletedEvent = stockDomainService.deleteOrder(order, deleteOrderCommand.getMessages());
        orderRepository.deleteOrder(orderId, vendorId);

        if (orderDeletedEvent.getOrder().getOrderStatus() != OrderStatus.PENDING) {
            financeOutboxHelper.saveOutboxMessage(
                    financeOutboxHelper.transformOrderDeletedEventToOrderStatusEventPayload(orderDeletedEvent),
                    orderDeletedEvent.getOrder().getOrderStatus(),
                    financeOutboxHelper.orderStatusToSagaStatus(orderDeletedEvent.getOrder().getOrderStatus()),
                    OutboxStatus.STARTED,
                    UUID.randomUUID()
            );
        }
        log.info("Deleted order with id: {}", order.getId().getValue());
    }

    public OrderListResponse fetchOrders(VendorId vendorId, int pageNumber, int pageSize) {
        return orders(orderRepository.findAll(vendorId, pageNumber, pageSize)
                .orElseThrow(() -> new OrderNotFoundException(Orders_Not_Found_Exception_Message)),orderRepository.countAll(vendorId));
    }

    public OrderListResponse fetchOrders(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, OrderSortField>> sort) {
        return orders(orderRepository.findAll(vendorId, pageNumber, pageSize, sort)
                .orElseThrow(() -> new OrderNotFoundException(Orders_Not_Found_Exception_Message)),orderRepository.countAll(vendorId));
    }

    private Product getExistingProduct(ProductId productId, VendorId vendorId) {
        return productRepository
                .findByIdAndVendorId(productId, vendorId)
                .orElseThrow(() -> new ProductNotFoundException("Product not available!"));
    }

    private Order getExistingOrder(OrderId orderId, VendorId vendorId) {
        return orderRepository.findByIdAndVendorId(orderId, vendorId).
                orElseThrow(() -> new OrderNotFoundException(Order_Not_Found_Exception_Message));
    }

    private OrderListResponse orders(List<Order> orders, long total) {
        return OrderListResponse.builder()
                .total(total)
                .list(orders.stream()
                        .map(orderDataMapper::transformOrderToOrderResponse)
                        .toList())
                .build();
    }

}
