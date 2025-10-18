package com.oliolishop.oliolishop.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {
    @Id
    @Column(name = "order_item_id")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_sku_id")
    ProductSku productSku;

    int quantity;

    int returnQuantity;

    BigDecimal unitPrice;

    boolean allowReturn;

    @OneToOne(mappedBy = "orderItem")
    Rating rating;
}
