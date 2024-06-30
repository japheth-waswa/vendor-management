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

import java.util.List;

public interface StockDomainService {
    ProductCategoryCreatedEvent createProductCategory(ProductCategory productCategory);

    ProductCategoryUpdatedEvent updateProductCategory(ProductCategory productCategory);

    ProductCategoryDeletedEvent deleteProductCategory(ProductCategory productCategory);

    ProductCreatedEvent createProduct(Product product, ProductCategory productCategory);

    ProductUpdatedEvent updateProduct(Product product, ProductCategory productCategory);

    ProductDeletedEvent deleteProduct(Product product);

    OrderCreatedEvent createOrder(Order order);

    OrderUpdateEvent modifyOrderItem(Order order, Product product, int quantity);

    OrderUpdateEvent deleteOrderItem(Order order, Product product);

    OrderUpdateEvent deleteOrderItem(Order order, OrderItemId orderItemId);

    OrderUpdateEvent settleOrder(Order order);

    OrderUpdateEvent cancelOrder(Order order, List<String> messages);

    OrderDeletedEvent deleteOrder(Order order, List<String> messages);
}
