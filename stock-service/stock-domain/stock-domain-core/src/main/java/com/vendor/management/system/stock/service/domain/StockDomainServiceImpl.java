package com.vendor.management.system.stock.service.domain;

import com.vendor.management.system.stock.service.domain.entity.Order;
import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.stock.service.domain.event.order.OrderCreatedEvent;
import com.vendor.management.system.stock.service.domain.event.order.OrderDeletedEvent;
import com.vendor.management.system.stock.service.domain.event.order.OrderUpdateEvent;
import com.vendor.management.system.stock.service.domain.event.product.ProductCreatedEvent;
import com.vendor.management.system.stock.service.domain.event.product.ProductDeletedEvent;
import com.vendor.management.system.stock.service.domain.event.product.ProductUpdatedEvent;
import com.vendor.management.system.stock.service.domain.event.product.ProductCategoryCreatedEvent;
import com.vendor.management.system.stock.service.domain.event.product.ProductCategoryDeletedEvent;
import com.vendor.management.system.stock.service.domain.event.product.ProductCategoryUpdatedEvent;
import com.vendor.management.system.domain.valueobject.OrderItemId;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.vendor.management.system.domain.util.DomainConstants.UTC;

@Slf4j
public class StockDomainServiceImpl implements StockDomainService {
    @Override
    public ProductCategoryCreatedEvent createProductCategory(ProductCategory productCategory) {
        productCategory.init();
        log.info("Product category with id: {} is created", productCategory.getId().getValue());
        return new ProductCategoryCreatedEvent(productCategory, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public ProductCategoryUpdatedEvent updateProductCategory(ProductCategory productCategory) {
        productCategory.update();
        log.info("Product category with id: {} is updated", productCategory.getId().getValue());
        return new ProductCategoryUpdatedEvent(productCategory, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public ProductCategoryDeletedEvent deleteProductCategory(ProductCategory productCategory) {
        productCategory.delete();
        log.info("Product category with id: {} is deletable", productCategory.getId().getValue());
        return new ProductCategoryDeletedEvent(productCategory, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public ProductCreatedEvent createProduct(Product product, ProductCategory productCategory) {
        product.init(productCategory);
        log.info("Product with id: {} is created", product.getId().getValue());
        return new ProductCreatedEvent(product, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public ProductUpdatedEvent updateProduct(Product product, ProductCategory productCategory) {
        product.update(productCategory);
        log.info("Product with id: {} and category with id: {} is updated", product.getId().getValue(), productCategory.getId().getValue());
        return new ProductUpdatedEvent(product, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public ProductDeletedEvent deleteProduct(Product product) {
        product.delete();
        log.info("Product with id: {} is deletable", product.getId().getValue());
        return new ProductDeletedEvent(product, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderCreatedEvent createOrder(Order order) {
        order.initializeOrder();
        log.info("Order with id: {} is initialized", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderUpdateEvent modifyOrderItem(Order order, Product product, int quantity) {
        order.modifyOrderItem(product, quantity);
        log.info("Product with id: {} has been added to the order with id: {}", product.getId().getValue(), order.getId().getValue());
        return new OrderUpdateEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderUpdateEvent deleteOrderItem(Order order, Product product) {
        order.removeOrderItem(product);
        log.info("Product with id {} has been removed from the order with id: {}", product.getId().getValue(), order.getId().getValue());
        return new OrderUpdateEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderUpdateEvent deleteOrderItem(Order order, OrderItemId orderItemId) {
        order.removeOrderItem(orderItemId);
        log.info("Order item with id: {} has been removed from the order with id: {}",
                orderItemId.getValue(), order.getId().getValue());
        return new OrderUpdateEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderUpdateEvent settleOrder(Order order) {
        order.settle();
        log.info("Order with id: {} can be settled", order.getId().getValue());
        return new OrderUpdateEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderUpdateEvent cancelOrder(Order order, List<String> messages) {
        order.cancel(messages);
        log.info("Order with id: {} is cancelable", order.getId().getValue());
        return new OrderUpdateEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderDeletedEvent deleteOrder(Order order, List<String> messages) {
        order.delete(messages);
        log.info("Order with id: {} is deletable", order.getId().getValue());
        return new OrderDeletedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }
}
