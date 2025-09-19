package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    Brand brand;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    String description;

    @Column(name = "short_description", columnDefinition = "TEXT", nullable = false)
    String shortDescription;

    @OneToMany(mappedBy = "spu", fetch = FetchType.LAZY)
    Set<DescriptionAttr> attrs;


    @OneToMany(mappedBy = "spu", fetch = FetchType.LAZY)
    Set<ProductSku> productSkus;

    @OneToMany(mappedBy = "spu", fetch = FetchType.LAZY)
    Set<ProductSkuAttr> skuAttrs;

    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status")
    StockStatus stockStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status")
    DeleteStatus deleteStatus;

    @Column(name = "sort")
    Integer sort;

    @Column(name = "image", length = 500, nullable = false)
    String image;

    @Column(name = "media", columnDefinition = "TEXT")
    String media;

    @Column(name = "`key`", length = 500, unique = true, nullable = false)
    String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "create_date")
    LocalDateTime createDate;

    @Column(name = "update_date")
    LocalDateTime updateDate;

    @Column(name = "sold")
    Integer sold;

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

//    @Formula("(SELECT MIN(sku.original_price) FROM product_sku sku WHERE sku.product_spu_id = product_spu_id)")
//    private Double minPrice;

}
