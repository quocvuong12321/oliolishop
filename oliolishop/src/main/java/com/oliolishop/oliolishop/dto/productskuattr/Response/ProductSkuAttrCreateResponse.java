package com.oliolishop.oliolishop.dto.productskuattr.Response;


import com.oliolishop.oliolishop.dto.productskuattr.Request.ProductSkuAttrValueRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuAttrCreateResponse {
    String productSpuId;
    String name;
    Map<String, Set<ProductSkuAttrValueResponse>> attributes;
}
