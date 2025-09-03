package com.oliolishop.oliolishop.dto.productspu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSpuResponse {
    String id;
    String name;
    String image;
    double originalPrice;
    double price;
    int discountRate;
    int sold;
}
