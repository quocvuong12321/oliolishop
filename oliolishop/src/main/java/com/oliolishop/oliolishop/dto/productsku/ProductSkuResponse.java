package com.oliolishop.oliolishop.dto.productsku;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuResponse {

    String id;
    String skuCode;
    String image;
    String product_spu_id;
    double price;
    double originalPrice;
    double discountRate;
    int sort;
}
