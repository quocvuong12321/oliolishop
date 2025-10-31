package com.oliolishop.oliolishop.dto.order;

import com.oliolishop.oliolishop.dto.productsku.ProductSkuResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    String id;
    String productSkuId;
    String productSpuId;
    String name;
    String thumbnail;
    String variant;
    int quantity;
    BigDecimal unitPrice;
    int returnQuantity;
    boolean allowReturn;
    boolean isRated;
}
