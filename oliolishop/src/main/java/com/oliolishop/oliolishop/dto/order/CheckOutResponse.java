package com.oliolishop.oliolishop.dto.order;

import com.oliolishop.oliolishop.dto.address.AddressResponse;
import com.oliolishop.oliolishop.dto.cart.CartItemResponse;
import com.oliolishop.oliolishop.dto.payment.PaymentMethodResponse;
import com.oliolishop.oliolishop.dto.voucher.VoucherResponse;
import com.oliolishop.oliolishop.entity.Voucher;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CheckOutResponse {

    List<CheckOutItemResponse> checkOutItemResponses;

    BigDecimal finalAmount;

    BigDecimal discountAmount;

    BigDecimal totalAmount;

    String appliedVoucherCode;

    List<AddressResponse> address;

    List<PaymentMethodResponse> paymentMethod;

    List<VoucherResponse> vouchers;

    Double totalWeight;

    BigDecimal feeShip;

    LocalDateTime expectedDeliveryTime;

    public BigDecimal getTotalAmount(){
        if(checkOutItemResponses.isEmpty())
            return BigDecimal.ZERO;
        return checkOutItemResponses.stream().map(CheckOutItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add)
                .setScale(2,RoundingMode.HALF_UP);
    }

    public Double getTotalWeight(){
        return Math.round(checkOutItemResponses.stream().map(CheckOutItemResponse::getWeight).reduce(0.0, Double::sum)*100.0)/100.0;
    }

}
