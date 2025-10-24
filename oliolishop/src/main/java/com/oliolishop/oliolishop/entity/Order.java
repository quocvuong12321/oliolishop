package com.oliolishop.oliolishop.entity;


import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.enums.VoucherStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "`order`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Order {
    @Id
    @Column(name = "order_id")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    Customer customer;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    Voucher voucher;

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING) // hoặc không dùng EnumType nếu custom
    OrderStatus orderStatus;

    String voucherCode;

    BigDecimal feeShip;

    BigDecimal totalAmount;

    BigDecimal discountAmount;

    BigDecimal voucherDiscountAmount;

    BigDecimal finalAmount;

    @Column(name = "receiver_name", length = 100)
    String receiverName;

    @Column(name = "receiver_phone", length = 15)
    String receiverPhone;

    @Column(name = "shipping_street")
    String shippingStreet; // Địa chỉ chi tiết (số nhà, tên đường)

    @Column(name = "ward_id")
    String wardId; // Mã Phường/Xã (ví dụ: mã GHN)

    @Column(name = "district_id")
    String districtId; // Mã Quận/Huyện

    @Column(name = "province_id")
    String provinceId; // Mã Tỉnh/Thành phố

    String shippingAddress;

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

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirm_by")
    Employee confirmBy;

    LocalDateTime confirmDate;

}
