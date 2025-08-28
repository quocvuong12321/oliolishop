package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_sku_attr")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuAttr {

    @Id
    @Column(name = "product_sku_attr_id", length = 36)
    String id;

    @Column(name = "product_spu_id", length = 36, nullable = false)
    String productSpuId;

    @Column(name = "name", length = 128, nullable = false)
    String name;

    @Column(name = "value", length = 500, nullable = false)
    String value;

    @Column(name = "show_preview_image")
    Boolean showPreviewImage;

    @Column(name = "image", length = 500)
    String image;

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
}
