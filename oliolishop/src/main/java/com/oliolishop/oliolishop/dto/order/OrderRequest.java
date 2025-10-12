package com.oliolishop.oliolishop.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String voucherCode;
    BigDecimal feeShip;
    String addressId;
    String shippingAddress;
    List<OrderItemRequest> orderItems;
}
