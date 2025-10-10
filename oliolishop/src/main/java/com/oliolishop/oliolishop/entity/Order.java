package com.oliolishop.oliolishop.entity;


import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.enums.VoucherStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @Column(name = "order_id")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    Address address;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    Voucher voucher;


    OrderStatus orderStatus;

    String voucherCode;

    double feeShip;

    double totalAmount;

    double discountAmount;

    double voucherDiscountAmount;

    double finalAmount;

    String shippingAddress;

    @CreationTimestamp
    LocalDateTime createDate;

    @UpdateTimestamp
    LocalDateTime updateDate;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.ALL)
    List<OrderItem> orderItems;

}
