package com.vendor.management.system.stock.service.dataaccess.productcategory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_categories"
//        ,
//        indexes = {
//        @Index(name = "idx_vendorId", columnList = "vendorId"),
//        @Index(name = "idx_name", columnList = "name"),
//        @Index(name = "idx_updatedAt", columnList = "updatedAt"),
//        @Index(name = "idx_createdAt", columnList = "createdAt"),
//}
)
@Entity
public class ProductCategoryEntity {
    @Id
    private UUID id;
    private UUID vendorId;
    private String name;
    private ZonedDateTime updatedAt;
    private ZonedDateTime createdAt;
}
