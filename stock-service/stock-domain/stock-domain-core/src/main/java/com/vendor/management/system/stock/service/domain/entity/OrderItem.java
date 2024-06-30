package com.vendor.management.system.stock.service.domain.entity;

import com.vendor.management.system.stock.service.domain.exception.StockDomainException;
import com.vendor.management.system.domain.entity.BaseEntity;
import com.vendor.management.system.domain.valueobject.*;

public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final Product product;
    private int quantity;
    private Money price;
    private Money subTotal;
    private final CreatedAt createdAt;
    private final UpdatedAt updatedAt;

    public void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId);
        modifyQuantity(this.quantity);
    }

    public void modifyQuantity(int quantity) {
        if (product.getQuantity().getValue() < quantity) {
            throw new StockDomainException("Quantity must be less than the available stock");
        }

        this.quantity = quantity;
        this.price = product.getUnitPrice();
        this.subTotal = product.getUnitPrice().multiply(this.quantity);
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public Money getSubTotal() {
        return subTotal;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    public UpdatedAt getUpdatedAt() {
        return updatedAt;
    }

    private OrderItem(Builder builder) {
        setId(builder.orderItemId);
        orderId = builder.orderId;
        product = builder.product;
        quantity = builder.quantity;
        price = builder.price;
        subTotal = builder.subTotal;
        createdAt = builder.createdAt;
        updatedAt = builder.updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderItemId orderItemId;
        private OrderId orderId;
        private Product product;
        private int quantity;
        private Money price;
        private Money subTotal;
        private CreatedAt createdAt;
        private UpdatedAt updatedAt;

        private Builder() {
        }

        public Builder orderItemId(OrderItemId val) {
            orderItemId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder product(Product val) {
            product = val;
            return this;
        }

        public Builder quantity(int val) {
            quantity = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder subTotal(Money val) {
            subTotal = val;
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

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
