package com.oliolishop.oliolishop.dto.productspu;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageSearchResultDTO {
    String spu_id;
    double score;
}
