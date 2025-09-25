package com.oliolishop.oliolishop.dto.productskuattr.Request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuGenerateRequest {
    String productSpuId;
    Map<String, Set<ProductSkuAttrValueRequest>> attributes;
}
