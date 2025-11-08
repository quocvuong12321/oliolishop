package com.oliolishop.oliolishop.dto.order;

import com.oliolishop.oliolishop.dto.cart.CartItemRequest;
import com.oliolishop.oliolishop.entity.Address;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CheckOutRequest {

    List<CartItemRequest> cartItemRequests;
    String addressId;
    String voucherCode;
    boolean buyFromCart;
}

