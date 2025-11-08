package com.oliolishop.oliolishop.dto.voucher;

import com.oliolishop.oliolishop.enums.VoucherStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {

    String id;
    String name;
    String voucherCode;
    Double discountPercent;
    Double maxDiscountValue;
    Double minOrderValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    int amount;
    int usedCount;
    int maxUsagePerUser;
    LocalDateTime createDate;
    LocalDateTime updateDate;
    VoucherStatus status;

}
