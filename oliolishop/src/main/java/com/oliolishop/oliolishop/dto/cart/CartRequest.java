package com.oliolishop.oliolishop.dto.cart;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartRequest {

    String customerId;

    Set<CartItemRequest> cartItems;

}
