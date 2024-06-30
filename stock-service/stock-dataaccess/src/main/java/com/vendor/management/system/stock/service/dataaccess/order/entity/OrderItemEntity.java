package com.vendor.management.system.stock.service.dataaccess.order.entity;

import com.vendor.management.system.stock.service.dataaccess.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OrderItemEntity.class)
@Table(name = "order_items")
@Entity
public class OrderItemEntity {
    @Id
    private Long id;
    @Id
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;

//    private UUID productId;
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private ProductEntity product;

    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subTotal;
    private ZonedDateTime updatedAt;
    private ZonedDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderItemEntity that = (OrderItemEntity) o;
        return id.equals(that.id) && order.equals(that.order);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + order.hashCode();
        return result;
    }
}
