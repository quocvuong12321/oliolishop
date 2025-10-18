package com.oliolishop.oliolishop.dto.productsku;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuResponse {

    String id;
    String skuCode;
    String image;
    String productSpuId;
    BigDecimal originalPrice;
    int skuStock;
    int sort;
}
