package com.oliolishop.oliolishop.dto.productskuattr.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuAttrValueResponse {
    String id;
    String value;
    String image;
    boolean showPreviewImage;
}
