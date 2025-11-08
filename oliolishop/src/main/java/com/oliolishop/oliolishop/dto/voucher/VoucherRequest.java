package com.oliolishop.oliolishop.dto.voucher;


import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest {

    String voucherCode;
    String name;
    @Positive(message ="QUANTITY_POSITIVE")
    Double discountPercent;
    @Positive(message ="QUANTITY_POSITIVE")
    BigDecimal maxDiscountValue;
    @Positive(message ="QUANTITY_POSITIVE")
    BigDecimal minOrderValue;
    @Positive(message ="QUANTITY_POSITIVE")
    int amount;
    @Positive(message ="QUANTITY_POSITIVE")
    int maxUsagePerUser;
    LocalDateTime startDate;
    LocalDateTime endDate;

}
