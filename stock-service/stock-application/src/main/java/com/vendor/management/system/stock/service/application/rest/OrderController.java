package com.vendor.management.system.stock.service.application.rest;

import com.vendor.management.system.application.security.SecurityConfig;
import com.vendor.management.system.application.util.UrlHelper;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderListResponse;
import com.vendor.management.system.stock.service.domain.valueobject.OrderSortField;
import com.vendor.management.system.domain.valueobject.AbstractSortPayload;
import com.vendor.management.system.domain.valueobject.OrderId;
import com.vendor.management.system.domain.valueobject.SortDirection;
import com.vendor.management.system.domain.valueobject.VendorId;
import com.vendor.management.system.stock.service.domain.dto.order.*;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderResponse;
import com.vendor.management.system.stock.service.domain.ports.input.service.StockApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = UrlHelper.StockServiceUrl.ROOT_ORDER, produces = "application/vnd.api.v1+json")
public class OrderController {
    private final StockApplicationService stockApplicationService;
    private final SecurityConfig securityConfig;

    public OrderController(StockApplicationService stockApplicationService,
                           SecurityConfig securityConfig) {
        this.stockApplicationService = stockApplicationService;
        this.securityConfig = securityConfig;
    }

    @GetMapping
    public ResponseEntity<OrderResponse> fetchOrder(Authentication authentication, @RequestParam UUID orderId) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Fetching order with id: {},for vendor with id: {}", orderId, vendorId.getValue());
        OrderResponse orderResponse = stockApplicationService.fetchOrder(vendorId, new OrderId(orderId));
        log.info("Successfully fetched order with id: {},for vendor with id: {}", orderId, vendorId.getValue());
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping(UrlHelper.StockServiceUrl.ORDER_LIST)
    public ResponseEntity<OrderListResponse> ordersList(Authentication authentication, @RequestParam int pageNumber,
                                                        @RequestParam int pageSize,
                                                        @RequestParam(required = false) SortDirection sortDirection,
                                                        @RequestParam(required = false) OrderSortField sortField) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Searching orders with pageNumber: {}, pageSize: {}, sortDirection: {}, sortField: {}",
                pageNumber, pageSize, sortDirection, sortField);
        OrderListResponse orders;
        if (sortDirection != null && sortField != null) {
            orders = stockApplicationService.fetchOrders(vendorId, pageNumber, pageSize,
                    List.of(new AbstractSortPayload<>(sortDirection, sortField)));
        } else {
            orders = stockApplicationService.fetchOrders(vendorId, pageNumber, pageSize);
        }
        log.info("Found: {} orders with pageNumber: {}, pageSize: {}, sortDirection: {}, sortField:{}",
                orders.getList().size(), pageNumber, pageSize, sortDirection, sortField);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(Authentication authentication,
                                                     @RequestBody CreateOrderCommand createOrderCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Creating order with items: {} for vendor: {}", createOrderCommand.getItems(), vendorId.getValue());
        OrderResponse orderResponse = stockApplicationService.createOrder(vendorId, createOrderCommand);
        log.info("Created order with id: {}", orderResponse.getOrderId());
        return ResponseEntity.ok(orderResponse);
    }

    @PatchMapping
    public ResponseEntity<OrderResponse> modifyOrder(Authentication authentication,
                                                     @RequestBody ModifyOrderCommand modifyOrderCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Modifying order with id: {} for vendor: {} with product id: {} & quantity: {}",
                modifyOrderCommand.getOrderId(), vendorId.getValue(), modifyOrderCommand.getProductId(), modifyOrderCommand.getQuantity());
        OrderResponse orderResponse = stockApplicationService.modifyOrder(vendorId, modifyOrderCommand);
        log.info("Modified order with id: {}", orderResponse.getOrderId());
        return ResponseEntity.ok(orderResponse);
    }

    @PatchMapping(UrlHelper.StockServiceUrl.ORDER_SETTLE)
    public ResponseEntity<OrderResponse> settleOrder(Authentication authentication,
                                                     @RequestBody SettleOrderCommand settleOrderCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Settling order with id: {}", settleOrderCommand.getOrderId());
        OrderResponse orderResponse = stockApplicationService.settleOrder(vendorId, settleOrderCommand);
        log.info("Successfully settled order with id: {}", orderResponse.getOrderId());
        return ResponseEntity.ok(orderResponse);
    }

    @PatchMapping(UrlHelper.StockServiceUrl.ORDER_CANCEL)
    public ResponseEntity<OrderResponse> cancelOrder(Authentication authentication,
                                                     @RequestBody CancelOrderCommand cancelOrderCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Cancelling order with id: {}", cancelOrderCommand.getOrderId());
        OrderResponse orderResponse = stockApplicationService.cancelOrder(vendorId, cancelOrderCommand);
        log.info("Successfully cancelled order with id: {}", orderResponse.getOrderId());
        return ResponseEntity.ok(orderResponse);
    }

    @DeleteMapping(UrlHelper.StockServiceUrl.ORDER_REMOVE_PRODUCT)
    public ResponseEntity<OrderResponse> removeOrderItemByProduct(Authentication authentication,
                                                                  @RequestBody DeleteOrderProductCommand deleteOrderProductCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Delete product from order id: {} for vendor: {} with product id: {}",
                deleteOrderProductCommand.getOrderId(), vendorId.getValue(), deleteOrderProductCommand.getProductId());
        OrderResponse orderResponse = stockApplicationService.deleteOrderItem(vendorId, deleteOrderProductCommand);
        log.info("Successfully removed product with id: {} from order with id: {}", deleteOrderProductCommand.getProductId(), orderResponse.getOrderId());
        return ResponseEntity.ok(orderResponse);
    }

    @DeleteMapping(UrlHelper.StockServiceUrl.ORDER_REMOVE_ITEM)
    public ResponseEntity<OrderResponse> removeOrderItemByItemId(Authentication authentication,
                                                                 @RequestBody DeleteOrderItemCommand deleteOrderItemCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Delete product from order id: {} for vendor: {} with item id: {}",
                deleteOrderItemCommand.getOrderId(), vendorId.getValue(), deleteOrderItemCommand.getOrderItemId());
        OrderResponse orderResponse = stockApplicationService.deleteOrderItem(vendorId, deleteOrderItemCommand);
        log.info("Successfully removed item with id: {} from order with id: {}", deleteOrderItemCommand.getOrderItemId(), orderResponse.getOrderId());
        return ResponseEntity.ok(orderResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteOrder(Authentication authentication,
                                            @RequestBody DeleteOrderCommand deleteOrderCommand) {
        VendorId vendorId = securityConfig.getVendorId(authentication);
        log.info("Delete order id: {} with messages: {}", deleteOrderCommand.getOrderId(), deleteOrderCommand.getMessages());
        stockApplicationService.deleteOrder(vendorId, deleteOrderCommand);
        log.info("Successfully deleted order with id: {}", deleteOrderCommand.getOrderId());
        return ResponseEntity.ok().build();
    }

}
