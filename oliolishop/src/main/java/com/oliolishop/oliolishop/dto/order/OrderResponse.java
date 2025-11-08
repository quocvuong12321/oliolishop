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

    // --- Thông tin người nhận hàng ---
    String receiverName;
    String receiverPhone;

    // --- Thông tin địa chỉ chi tiết ---
    String shippingStreet;
    String wardId;
    String districtId;
    String provinceId;
    String shippingAddress; // Giữ lại cho mục đích hiển thị địa chỉ đầy đủ (text)

    // --- Thông tin thanh toán ---
    BigDecimal totalAmount;
    BigDecimal feeShip;
    BigDecimal discountAmount;
    BigDecimal voucherDiscountAmount;
    BigDecimal finalAmount;

    // --- Audit ---
    LocalDateTime createDate;
    LocalDateTime updateDate;

    List<OrderItemResponse> orderItems;

    boolean isRefundSuccess;
}
