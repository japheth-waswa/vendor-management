package com.vendor.management.system.stock.service.domain;

import com.vendor.management.system.domain.valueobject.*;
import com.vendor.management.system.stock.service.domain.command.OrderCommandHandler;
import com.vendor.management.system.stock.service.domain.command.ProductCategoryCommandHandler;
import com.vendor.management.system.stock.service.domain.command.ProductCommandHandler;
import com.vendor.management.system.stock.service.domain.dto.order.*;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderListResponse;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderResponse;
import com.vendor.management.system.stock.service.domain.dto.product.*;
import com.vendor.management.system.stock.service.domain.dto.productcategory.*;
import com.vendor.management.system.stock.service.domain.ports.input.service.StockApplicationService;
import com.vendor.management.system.stock.service.domain.valueobject.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
public class StockApplicationServiceImpl implements StockApplicationService {
    private final ProductCategoryCommandHandler productCategoryCommandHandler;
    private final ProductCommandHandler productCommandHandler;
    private final OrderCommandHandler orderCommandHandler;

    public StockApplicationServiceImpl(ProductCategoryCommandHandler productCategoryCommandHandler,
                                       ProductCommandHandler productCommandHandler,
                                       OrderCommandHandler orderCommandHandler) {
        this.productCategoryCommandHandler = productCategoryCommandHandler;
        this.productCommandHandler = productCommandHandler;
        this.orderCommandHandler = orderCommandHandler;
    }

    @Override
    public ProductCategoryResponse createProductCategory(VendorId vendorId, CreateProductCategoryCommand createProductCategoryCommand) {
        return productCategoryCommandHandler.createProductCategory(vendorId, createProductCategoryCommand);
    }

    @Override
    public ProductCategoryResponse updateProductCategory(VendorId vendorId, UpdateProductCategoryCommand updateProductCategoryCommand) {
        return productCategoryCommandHandler.updateProductCategory(vendorId, updateProductCategoryCommand);
    }

    @Override
    public ProductCategoryResponse deleteProductCategory(VendorId vendorId, DeleteProductCategoryCommand deleteProductCategoryCommand) {
        return productCategoryCommandHandler.deleteProductCategory(vendorId, deleteProductCategoryCommand);
    }

    @Override
    public ProductCategoryListResponse fetchCategories(VendorId vendorId, int pageNumber, int pageSize) {
        return productCategoryCommandHandler.fetchProductCategories(vendorId, pageNumber, pageSize);
    }

    @Override
    public ProductCategoryListResponse fetchCategories(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductCategorySortField>> sort) {
        return productCategoryCommandHandler.fetchProductCategories(vendorId, pageNumber, pageSize, sort);
    }

    @Override
    public ProductResponse createProduct(VendorId vendorId, CreateProductCommand createProductCommand) {
        return productCommandHandler.createProduct(vendorId, createProductCommand);
    }

    @Override
    public ProductResponse updateProduct(VendorId vendorId, UpdateProductCommand updateProductCommand) {
        return productCommandHandler.updateProduct(vendorId, updateProductCommand);
    }

    @Override
    public ProductResponse deleteProduct(VendorId vendorId, DeleteProductCommand deleteProductCommand) {
        return productCommandHandler.deleteProduct(vendorId, deleteProductCommand);
    }

    @Override
    public ProductListResponse fetchProducts(VendorId vendorId, int pageNumber, int pageSize) {
        return productCommandHandler.fetchProducts(vendorId, pageNumber, pageSize);
    }

    @Override
    public ProductListResponse fetchProducts(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductSortField>> sort) {
        return productCommandHandler.fetchProducts(vendorId, pageNumber, pageSize, sort);
    }

    @Override
    public ProductListResponse fetchProducts(VendorId vendorId, ProductName productName, ProductStatus productStatus, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, ProductSortField>> sort) {
        return productCommandHandler.fetchProducts(vendorId, productName, productStatus, pageNumber, pageSize, sort);
    }

    @Override
    public OrderResponse fetchOrder(VendorId vendorId, OrderId orderId) {
        return orderCommandHandler.fetchOrder(vendorId, orderId);
    }

    @Override
    public OrderResponse createOrder(VendorId vendorId, CreateOrderCommand createOrderCommand) {
        return orderCommandHandler.createOrder(vendorId, createOrderCommand);
    }

    @Override
    public OrderResponse modifyOrder(VendorId vendorId, ModifyOrderCommand modifyOrderCommand) {
        return orderCommandHandler.modifyOrder(vendorId, modifyOrderCommand);
    }

    @Override
    public OrderResponse deleteOrderItem(VendorId vendorId, DeleteOrderProductCommand deleteOrderProductCommand) {
        return orderCommandHandler.deleteOrderItem(vendorId, deleteOrderProductCommand);
    }

    @Override
    public OrderResponse deleteOrderItem(VendorId vendorId, DeleteOrderItemCommand deleteOrderItemCommand) {
        return orderCommandHandler.deleteOrderItem(vendorId, deleteOrderItemCommand);
    }

    @Override
    public OrderResponse settleOrder(VendorId vendorId, SettleOrderCommand settleOrderCommand) {
        return orderCommandHandler.settleOrder(vendorId, settleOrderCommand);
    }

    @Override
    public OrderResponse cancelOrder(VendorId vendorId, CancelOrderCommand cancelOrderCommand) {
        return orderCommandHandler.cancelOrder(vendorId, cancelOrderCommand);
    }

    @Override
    public void deleteOrder(VendorId vendorId, DeleteOrderCommand deleteOrderCommand) {
        orderCommandHandler.deleteOrder(vendorId, deleteOrderCommand);
    }

    @Override
    public OrderListResponse fetchOrders(VendorId vendorId, int pageNumber, int pageSize) {
        return orderCommandHandler.fetchOrders(vendorId, pageNumber, pageSize);
    }

    @Override
    public OrderListResponse fetchOrders(VendorId vendorId, int pageNumber, int pageSize, List<AbstractSortPayload<SortDirection, OrderSortField>> sort) {
        return orderCommandHandler.fetchOrders(vendorId, pageNumber, pageSize, sort);
    }
}
