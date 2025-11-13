package com.oliolishop.oliolishop.dto.productspu;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpuResponse {
     String id;          // product_spu_id
     String name;        // name
     String image;       // image
     String brandId;     // brand_id
     String categoryId;  // category_id
     double minPrice;    // min_price (MIN of sku.original_price)
     double maxPrice;    // max_price (MAX of sku.original_price)
     double score;
     String deleteStatus;
}
