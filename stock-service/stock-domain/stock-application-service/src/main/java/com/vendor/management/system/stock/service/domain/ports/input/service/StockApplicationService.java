package com.vendor.management.system.stock.service.domain.ports.input.service;

import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.OrderId;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.dto.order.*;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderListResponse;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderResponse;
import com.vendor.management.system.stock.service.domain.dto.product.*;
import com.vendor.management.system.stock.service.domain.dto.productcategory.*;
import com.vendor.management.system.stock.service.domain.valueobject.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface StockApplicationService {
    ProductCategoryResponse createProductCategory(@NotNull VendorId vendorId, @Valid CreateProductCategoryCommand createProductCategoryCommand);

    ProductCategoryResponse updateProductCategory(@NotNull VendorId vendorId, @Valid UpdateProductCategoryCommand updateProductCategoryCommand);

    ProductCategoryResponse deleteProductCategory(@NotNull VendorId vendorId, @Valid DeleteProductCategoryCommand deleteProductCategoryCommand);

    ProductCategoryListResponse fetchCategories(@NotNull VendorId vendorId, @Min(0) int pageNumber, @Min(1) @Max(50) int pageSize);

    ProductCategoryListResponse fetchCategories(@NotNull VendorId vendorId, @Min(0) int pageNumber, @Min(1) @Max(50) int pageSize, @NotNull @NotEmpty List<AbstractSortPayload<SortDirection, ProductCategorySortField>> sort);

    ProductResponse createProduct(@NotNull VendorId vendorId, @Valid CreateProductCommand createProductCommand);

    ProductResponse updateProduct(@NotNull VendorId vendorId, @Valid UpdateProductCommand updateProductCommand);

    ProductResponse deleteProduct(@NotNull VendorId vendorId, @Valid DeleteProductCommand deleteProductCommand);

    ProductListResponse fetchProducts(@NotNull VendorId vendorId, @Min(0) int pageNumber, @Min(1) @Max(50) int pageSize);

    ProductListResponse fetchProducts(@NotNull VendorId vendorId, @Min(0) int pageNumber, @Min(1) @Max(50) int pageSize, @NotNull @NotEmpty List<AbstractSortPayload<SortDirection, ProductSortField>> sort);

    ProductListResponse fetchProducts(@NotNull VendorId vendorId, @NotNull ProductName productName, @NotNull ProductStatus productStatus, @Min(0) int pageNumber, @Min(1) @Max(50) int pageSize, @NotNull @NotEmpty List<AbstractSortPayload<SortDirection, ProductSortField>> sort);

    OrderResponse fetchOrder(@NotNull VendorId vendorId, @NotNull OrderId orderId);

    OrderResponse createOrder(@NotNull VendorId vendorId, @Valid CreateOrderCommand createOrderCommand);

    OrderResponse modifyOrder(@NotNull VendorId vendorId, @Valid ModifyOrderCommand modifyOrderCommand);

    OrderResponse deleteOrderItem(@NotNull VendorId vendorId, @Valid DeleteOrderProductCommand deleteOrderProductCommand);

    OrderResponse deleteOrderItem(@NotNull VendorId vendorId, @Valid DeleteOrderItemCommand deleteOrderItemCommand);

    OrderResponse settleOrder(@NotNull VendorId vendorId, @Valid SettleOrderCommand settleOrderCommand);

    OrderResponse cancelOrder(@NotNull VendorId vendorId, @Valid CancelOrderCommand cancelOrderCommand);

    void deleteOrder(@NotNull VendorId vendorId, @Valid DeleteOrderCommand deleteOrderCommand);

    OrderListResponse fetchOrders(@NotNull VendorId vendorId, @Min(0) int pageNumber, @Min(1) @Max(50) int pageSize);

    OrderListResponse fetchOrders(@NotNull VendorId vendorId, @Min(0) int pageNumber, @Min(1) @Max(50) int pageSize, @NotNull @NotEmpty List<AbstractSortPayload<SortDirection, OrderSortField>> sort);

}
