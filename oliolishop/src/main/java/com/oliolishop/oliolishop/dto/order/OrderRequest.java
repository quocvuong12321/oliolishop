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
    String receiverName;
    String receiverPhone;
    String provinceId;
    String districtId;
    String wardId;
    String shippingStreet;
    String shippingAddress;
    String voucherCode;
    BigDecimal feeShip;
    List<OrderItemRequest> orderItems;
    BigDecimal loyalPoint;
    boolean buyFromCart;
}
