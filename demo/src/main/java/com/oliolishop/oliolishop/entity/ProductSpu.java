package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_spu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpu {

    @Id
    @Column(name = "product_spu_id", length = 36)
    String id;

    @Column(name = "name", columnDefinition = "TEXT", nullable = false)
    String name;

    @Column(name = "brand_id", length = 36, nullable = false)
    String brandId;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    String description;

    @Column(name = "short_description", columnDefinition = "TEXT", nullable = false)
    String shortDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status")
    StockStatus stockStatus = StockStatus.InStock;

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status")
    DeleteStatus deleteStatus = DeleteStatus.Active;

    @Column(name = "sort")
    Integer sort;

    @Column(name = "image", length = 500, nullable = false)
    String image;

    @Column(name = "media", columnDefinition = "TEXT")
    String media;

    @Column(name = "`key`", length = 500, unique = true, nullable = false)
    String key;

    @Column(name = "category_id", length = 36, nullable = false)
    String categoryId;

    @Column(name = "create_date")
    LocalDateTime createDate;

    @Column(name = "update_date")
    LocalDateTime updateDate;

    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }

    public enum StockStatus {
        InStock, OutOfStock
    }

    public enum DeleteStatus {
        Active, Deleted
    }
}
