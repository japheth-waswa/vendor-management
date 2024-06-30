package com.vendor.management.system.stock.service.dataaccess.order.mapper;

import com.vendor.management.system.stock.service.dataaccess.order.entity.OrderEntity;
import com.vendor.management.system.stock.service.dataaccess.order.entity.OrderItemEntity;
import com.vendor.management.system.stock.service.dataaccess.product.mapper.ProductDataAccessMapper;
import com.vendor.management.system.stock.service.domain.entity.Order;
import com.vendor.management.system.stock.service.domain.entity.OrderItem;
import com.vendor.management.system.stock.service.domain.valueobject.OrderSortField;
import com.vendor.management.system.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.vendor.management.system.domain.util.DomainConstants.FAILURE_MESSAGE_DELIMITER;
import static com.vendor.management.system.domain.util.DomainConstants.UTC;

@Component
public class OrderDataAccessMapper {
    private final ProductDataAccessMapper productDataAccessMapper;

    public OrderDataAccessMapper(ProductDataAccessMapper productDataAccessMapper) {
        this.productDataAccessMapper = productDataAccessMapper;
    }

    public OrderEntity orderToOrderEntityCreate(Order order) {
        return orderToOrderEntity(order, false);
    }

    public OrderEntity orderToOrderEntityUpdate(Order order) {
        return orderToOrderEntity(order, true);
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .vendorId(new VendorId(orderEntity.getVendorId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .price(new Money(orderEntity.getPrice()))
                .orderStatus(orderEntity.getOrderStatus())
                .orderItems(orderItemEntitiesToOrderItems(orderEntity.getItems()))
                .failureMessages(orderEntity.getFailureMessages().isEmpty() ? new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages()
                                .split(FAILURE_MESSAGE_DELIMITER))))
                .updatedAt(new UpdatedAt(orderEntity.getUpdatedAt()))
                .createdAt(new CreatedAt(orderEntity.getCreatedAt()))
                .build();
    }

    private OrderEntity orderToOrderEntity(Order order, boolean isUpdate) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .vendorId(order.getVendorId().getValue())
                .customerId(order.getCustomerId().getValue())
                .price(order.getPrice().getAmount())
                .orderStatus(order.getOrderStatus())
                .items(orderItemsToOrderItemEntities(order.getOrderItems(), isUpdate))
                .failureMessages(order.getFailureMessages() != null ?
                        String.join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : "")
                .updatedAt(order.getUpdatedAt() != null && !isUpdate ? order.getUpdatedAt().getValue() : ZonedDateTime.now(ZoneId.of(UTC)))
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().getValue() : ZonedDateTime.now(ZoneId.of(UTC)))
                .build();
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
        return orderEntity;
    }

    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> orderItems, boolean isUpdate) {
        return orderItems.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .product(!isUpdate ? productDataAccessMapper.productToProductEntity(orderItem.getProduct()) :
                                productDataAccessMapper.productToProductEntityUpdate(orderItem.getProduct()))
                        .quantity(orderItem.getQuantity())
                        .price(orderItem.getPrice().getAmount())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .updatedAt(orderItem.getUpdatedAt() != null && !isUpdate ? orderItem.getUpdatedAt().getValue() : ZonedDateTime.now(ZoneId.of(UTC)))
                        .createdAt(orderItem.getCreatedAt() != null ? orderItem.getCreatedAt().getValue() : ZonedDateTime.now(ZoneId.of(UTC)))
                        .build())
                .collect(Collectors.toList());
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(orderItemEntity -> OrderItem.builder()
                        .orderItemId(new OrderItemId(orderItemEntity.getId()))
                        .quantity(orderItemEntity.getQuantity())
                        .price(new Money(orderItemEntity.getPrice()))
                        .subTotal(new Money(orderItemEntity.getSubTotal()))
                        .product(productDataAccessMapper.productEntityToProduct(orderItemEntity.getProduct()))
                        .updatedAt(new UpdatedAt(orderItemEntity.getUpdatedAt()))
                        .createdAt(new CreatedAt(orderItemEntity.getCreatedAt()))
                        .build())
                .collect(Collectors.toList());
    }
    public String orderSortFieldToOrderEntitySortField(OrderSortField sortField) {
        return switch ((sortField)) {
            case STATUS -> "orderStatus";
            case PRICE -> "price";
            case CREATED_AT -> "createdAt";
            default -> "updatedAt";
        };
    }
}
