package com.vendor.management.system.stock.service.domain.mapper;

import com.vendor.management.system.stock.service.domain.entity.Order;
import com.vendor.management.system.stock.service.domain.entity.OrderItem;
import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.exception.ProductNotFoundException;
import com.vendor.management.system.stock.service.domain.valueobject.ProductId;
import com.vendor.management.system.domain.valueobject.CustomerId;
import com.vendor.management.system.domain.valueobject.Money;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.dto.order.CreateOrderCommand;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderItemResponse;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderResponse;
import com.vendor.management.system.stock.service.domain.ports.output.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderDataMapper {
    private final ProductRepository productRepository;

    public OrderDataMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Order transformCreateOrderCommandToOrder(VendorId vendorId, CreateOrderCommand createOrderCommand) {
        Map<ProductId, Product> products = new HashMap<>();
        return Order.builder()
                .vendorId(vendorId)
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .orderItems(createOrderCommand.getItems().stream()
                        .map(item -> {
                            ProductId productId = new ProductId(item.getProductId());
                            Product product = products.get(productId);
                            if (product == null) {
                                //fetch the product from repository and put it in the map.
                                product = productRepository.findByIdAndVendorId(productId, vendorId)
                                        .orElseThrow(() -> new ProductNotFoundException("Product in order items not found!"));
                                products.put(productId, product);
                            }

                            return OrderItem.builder()
                                    .product(product)
                                    .quantity(item.getQuantity())
                                    .price(new Money(item.getPrice()))
                                    .subTotal(new Money(item.getSubTotal()))
                                    .build();
                        })
                        .toList())
                .build();
    }

    public OrderResponse transformOrderToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId().getValue())
                .orderStatus(order.getOrderStatus())
                .customerId(order.getCustomerId().getValue())
                .price(order.getPrice().getAmount())
                .updatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt().getValue() : null)
                .createdAt(order.getCreatedAt() != null ? order.getCreatedAt().getValue() : null)
                .items(order.getOrderItems() == null ? null : order.getOrderItems().stream()
                        .map(item -> OrderItemResponse.builder()
                                .orderItemId(item.getId().getValue())
                                .productId(item.getProduct().getId().getValue())
                                .productName(item.getProduct().getProductName().getValue())
                                .quantity(item.getQuantity())
                                .price(item.getPrice().getAmount())
                                .subTotal(item.getSubTotal().getAmount())
                                .build())
                        .toList())
                .build();
    }

}
