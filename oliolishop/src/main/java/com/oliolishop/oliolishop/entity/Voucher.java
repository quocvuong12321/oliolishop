package com.oliolishop.oliolishop.entity;

import com.oliolishop.oliolishop.enums.VoucherStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher {
    @Id
    @Column(name = "voucher_id", length = 36, nullable = false)
    String voucherId;

    @Column(name = "name", length = 128)
    String name;

    @Column(name = "voucher_code", length = 50, nullable = false, unique = true)
    String voucherCode;

    @Column(name = "discount_percent", nullable = false)
    Double discountPercent;

    @Column(name = "max_discount_value", nullable = false)
    double maxDiscountValue;

    @Column(name = "min_order_value")
    double minOrderValue;

    @Column(name = "start_date", nullable = false)
    LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    LocalDateTime endDate;

    @Column(name = "amount")
    Integer amount;

    @Column(name = "create_date")
    LocalDateTime createDate;

    @Column(name = "update_date")
    LocalDateTime updateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    VoucherStatus status;


}
