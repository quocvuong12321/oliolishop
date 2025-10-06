package com.oliolishop.oliolishop.dto.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

    List<CartItemResponse> cartItems;

    int distinctProductCount;

    double totalAmount;

    double totalQuantity;

    public double getTotalAmount()
    {
        return cartItems.stream().mapToDouble(CartItemResponse::getTotalPrice).sum();
    }

    public int getTotalQuantity() {
        return cartItems.stream().mapToInt(CartItemResponse::getQuantity).sum();
    }

    public int getDistinctProductCount() {
        return cartItems.size(); // mỗi CartItem tương ứng 1 SKU khác nhau
    }
}
