package com.oliolishop.oliolishop.dto.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    BigDecimal totalAmount;

    int totalQuantity;

    public BigDecimal getTotalAmount()
    {
        if (cartItems == null || cartItems.isEmpty()) return BigDecimal.ZERO;
        return cartItems.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public int getTotalQuantity() {
        return cartItems.stream().mapToInt(CartItemResponse::getQuantity).sum();
    }

    public int getDistinctProductCount() {
        return cartItems.size(); // mỗi CartItem tương ứng 1 SKU khác nhau
    }
}
