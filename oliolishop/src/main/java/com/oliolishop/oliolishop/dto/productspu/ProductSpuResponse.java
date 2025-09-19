package com.oliolishop.oliolishop.dto.productspu;

import com.oliolishop.oliolishop.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
