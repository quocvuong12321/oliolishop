package com.oliolishop.oliolishop.dto.cart;


import com.oliolishop.oliolishop.util.AppUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {

    long id;
    String productSpuId;
    String productSkuId;
    String name;
    String thumbnail;
    String variant;
    BigDecimal price;

    int quantity;

    BigDecimal totalPrice;

    public BigDecimal getTotalPrice(){
        return price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
}
