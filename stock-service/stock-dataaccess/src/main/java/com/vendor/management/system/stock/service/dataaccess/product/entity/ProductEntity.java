package com.vendor.management.system.stock.service.dataaccess.product.entity;

import com.vendor.management.system.stock.service.domain.valueobject.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Entity
public class ProductEntity {
    @Id
    private UUID id;
    private UUID vendorId;
    private UUID categoryId;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;
    private String fileUrl;
    private ZonedDateTime updatedAt;
    private ZonedDateTime createdAt;
}
