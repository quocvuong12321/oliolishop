package com.oliolishop.oliolishop.dto.payment;


import com.oliolishop.oliolishop.constant.ApiPath;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    int amount;
    String orderInfor = "Thanh toán đơn hàng #%s";
    String returnUrl = ApiPath.Payment.ROOT+ApiPath.Payment.VNPAY_RETURN;
}
