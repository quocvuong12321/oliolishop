package com.oliolishop.oliolishop.dto.productsku;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuDeleteRequest {
    List<String> lstSkuId;
}
