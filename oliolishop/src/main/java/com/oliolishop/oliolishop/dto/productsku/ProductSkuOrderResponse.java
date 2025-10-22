package com.oliolishop.oliolishop.dto.productsku;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuOrderResponse {
    String id;
    String productSkuId;
    String thumbnail;
    String name;
    String variant;
    int quantity;
    BigDecimal unitPrice;

}
