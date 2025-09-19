package com.oliolishop.oliolishop.dto.productskuattr;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuAttrResponse {
    String id;
//    String productSpuId;
    String name;
    String value;
    String image;
    String showPreviewImage;
}
