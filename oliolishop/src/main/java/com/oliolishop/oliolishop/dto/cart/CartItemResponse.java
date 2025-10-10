package com.oliolishop.oliolishop.dto.cart;


import com.oliolishop.oliolishop.util.AppUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {

    long id;
    String productSpuId;
    String productSkuId;
    String name;
    String thumbnail;
    String variant;
    double price;

    int quantity;

    double totalPrice;



    public double getTotalPrice(){
        return AppUtils.round(price*quantity,2);
    }
}
