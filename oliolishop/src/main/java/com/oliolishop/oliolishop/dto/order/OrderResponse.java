package com.oliolishop.oliolishop.dto.order;

import com.oliolishop.oliolishop.entity.PaymentMethod;
import com.oliolishop.oliolishop.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    OrderStatus status;
    BigDecimal totalAmount;
    BigDecimal feeShip;
    BigDecimal discountAmount;
    BigDecimal voucherDiscountAmount;
    BigDecimal finalAmount;
    String shippingAddress;
    LocalDateTime createDate;
    List<OrderItemResponse> orderItems;
}
