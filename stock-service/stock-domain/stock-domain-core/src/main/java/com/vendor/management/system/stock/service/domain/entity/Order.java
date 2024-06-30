package com.vendor.management.system.stock.service.domain.entity;

import com.vendor.management.system.stock.service.domain.exception.StockDomainException;
import com.vendor.management.system.stock.service.domain.valueobject.ProductId;
import com.vendor.management.system.stock.service.domain.valueobject.ProductStatus;
import com.vendor.management.system.domain.entity.AggregateRoot;
import com.vendor.management.system.domain.valueobject.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Order extends AggregateRoot<OrderId> {
    private final VendorId vendorId;
    private final CustomerId customerId;
    private Money price;
    private List<OrderItem> orderItems;
    private OrderStatus orderStatus;
    private List<String> failureMessages;
    private final CreatedAt createdAt;
    private final UpdatedAt updatedAt;

    public void initializeOrder() {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new StockDomainException("At-least one order item must be provided!");
        }
        setId(new OrderId(UUID.randomUUID()));
        orderStatus = OrderStatus.PENDING;
        failureMessages = Collections.emptyList();
        validateVendor();
        validateCustomer();
        initializeOrderItems();
        computePrice();
    }

    public void modifyOrderItem(Product product, int quantity) {
        if (orderStatus != OrderStatus.PENDING) {
            throw new StockDomainException("Order is not in correct state for order item modification!");
        }

        if (!product.getProductStatus().equals(ProductStatus.ACTIVE)) {
            throw new StockDomainException("Product: " + product.getProductName().getValue() + " is not active!");
        }

        boolean productExists = false;
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProduct().getId().equals(product.getId())) {
                if (!orderItem.getProduct().getProductStatus().equals(ProductStatus.ACTIVE)) {
                    throw new StockDomainException("Product: " + orderItem.getProduct().getProductName().getValue() + " is not active!");
                }
                orderItem.modifyQuantity(quantity);
                productExists = true;
                computePrice();
                break;
            }
        }
        if (productExists) {
            return;
        }

        //does not exist, initialize.
        OrderItem orderItem = OrderItem.builder()
                .orderId(getId())
                .product(product)
                .quantity(quantity)
                .build();
        orderItems.add(orderItem);

        //initialize order items
        initializeOrderItems();
        //compute price
        computePrice();
    }

    public void removeOrderItem(OrderItemId orderItemId) {
        if (orderItems.isEmpty()) {
            throw new StockDomainException("Order items are empty!");
        }
        if (orderStatus != OrderStatus.PENDING) {
            throw new StockDomainException("Order is not in correct state for order item removal!");
        }
        boolean removedSuccessfully = false;
        Iterator<OrderItem> itemsIterator = orderItems.iterator();
        while (itemsIterator.hasNext()) {
            OrderItem orderItem = itemsIterator.next();
            if (orderItem.getId().equals(orderItemId)) {
                itemsIterator.remove();
                removedSuccessfully = true;
                break;
            }
        }
        if (removedSuccessfully) {
            //compute price
            computePrice();
            return;
        }
        throw new StockDomainException("Order item does not exist!");
    }

    public void removeOrderItem(Product product) {
        if (orderItems.isEmpty()) {
            throw new StockDomainException("Order items are empty!");
        }
        if (orderStatus != OrderStatus.PENDING) {
            throw new StockDomainException("Order is not in correct state for product removal!");
        }
        boolean removedSuccessfully = false;
        Iterator<OrderItem> itemsIterator = orderItems.iterator();
        while (itemsIterator.hasNext()) {
            OrderItem orderItem = itemsIterator.next();
            if (orderItem.getProduct().getId().equals(product.getId())) {
                itemsIterator.remove();
                removedSuccessfully = true;
                break;
            }
        }
        if (removedSuccessfully) {
            //compute price
            computePrice();
            return;
        }
        throw new StockDomainException("Product does not exist in the current order!");
    }

    public void settle() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new StockDomainException("Order is not in correct state for settle operation!");
        }
        orderStatus = OrderStatus.SETTLED;
    }

    public void cancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.SETTLED && orderStatus != OrderStatus.PENDING) {
            throw new StockDomainException("Order is not in correct state for cancel operation!");
        }
        if (failureMessages == null || failureMessages.isEmpty()) {
            throw new StockDomainException("Failure messages are required for order cancellation!");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    public void delete(List<String> failureMessages) {
        if (orderStatus != OrderStatus.PENDING) {
            throw new StockDomainException("Order is not in correct state for deletion!");
        }
        if (failureMessages == null || failureMessages.isEmpty()) {
            throw new StockDomainException("Failure messages are required for order deletion!");
        }
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isBlank()).toList());
        }
        if (this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }

    private void computePrice() {
        this.price = orderItems.stream().map(OrderItem::getSubTotal).reduce(Money.ZERO, Money::add);
    }

    private void validateCustomer() {
        if (customerId == null || customerId.getValue() == null) {
            throw new StockDomainException("Customer Id is required!");
        }
    }

    private void validateVendor() {
        if (vendorId == null || vendorId.getValue() == null) {
            throw new StockDomainException("Vendor Id is required!");
        }
    }

    private void initializeOrderItems() {
        if (orderItems == null || orderItems.isEmpty()) return;

        AtomicLong itemId = new AtomicLong(1);
        Map<ProductId, Product> products = new HashMap<>();
        orderItems = orderItems.stream().filter(orderItem -> {
                    if (products.containsKey(orderItem.getProduct().getId())) return false;
                    if (!orderItem.getProduct().getProductStatus().equals(ProductStatus.ACTIVE)) return false;
                    products.put(orderItem.getProduct().getId(), orderItem.getProduct());
                    return true;
                })
                .peek(orderItem -> orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId.getAndIncrement())))
                .collect(Collectors.toList());
    }

    public VendorId getVendorId() {
        return vendorId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public UpdatedAt getUpdatedAt() {
        return updatedAt;
    }

    private Order(Builder builder) {
        setId(builder.orderId);
        vendorId = builder.vendorId;
        customerId = builder.customerId;
        price = builder.price;
        orderItems = builder.orderItems;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
        createdAt = builder.createdAt;
        updatedAt = builder.updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderId orderId;
        private VendorId vendorId;
        private CustomerId customerId;
        private Money price;
        private List<OrderItem> orderItems;
        private OrderStatus orderStatus;
        private List<String> failureMessages;
        private CreatedAt createdAt;
        private UpdatedAt updatedAt;

        private Builder() {
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder vendorId(VendorId val) {
            vendorId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder orderItems(List<OrderItem> val) {
            orderItems = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Builder createdAt(CreatedAt val) {
            createdAt = val;
            return this;
        }

        public Builder updatedAt(UpdatedAt val) {
            updatedAt = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
