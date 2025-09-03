package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_sku")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSku {

    @Id
    @Column(name = "product_sku_id", length = 36)
    String id;

    @Column(name = "sku_code", length = 100, nullable = false, unique = true)
    String skuCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_spu_id")
    private ProductSpu spu;

    @Column(name = "price", nullable = false)
    Double price;

    @Column(name = "original_price", nullable = false)
    Double originalPrice;

    @Column(name = "discount_rate", nullable = false)
    Double discountRate;

    @Column(name = "sku_stock")
    Integer skuStock;

    @Column(name = "image", length = 500)
    String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status = Status.Active;

    @Column(name = "sort")
    Integer sort;

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

    public enum Status {
        Active, Inactive
    }
}
