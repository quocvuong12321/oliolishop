package com.oliolishop.oliolishop.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CheckOutItemResponse {
    String productSpuId;
    String productSkuId;
    String name;
    String thumbnail;
    String variant;
    BigDecimal price;
    Double weight;

    int quantity;

    BigDecimal totalPrice;
    public BigDecimal getTotalPrice(){
        return price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
}
