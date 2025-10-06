package com.oliolishop.oliolishop.dto.cart;


import com.oliolishop.oliolishop.exception.ErrorCode;
import jakarta.validation.constraints.Positive;
import jdk.jfr.Unsigned;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequest {

    String productSkuId;

    @Positive(message ="QUANTITY_POSITIVE")
    int quantity;


}
