package com.oliolishop.oliolishop.dto.cart;


import com.oliolishop.oliolishop.dto.productsku.ProductSkuResponse;
import com.oliolishop.oliolishop.dto.productskuattr.ProductSkuAttrResponse;
import com.oliolishop.oliolishop.dto.productskuattr.Response.ProductSkuAttrCreateResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuResponse;
import com.oliolishop.oliolishop.entity.ProductSkuAttr;
import com.oliolishop.oliolishop.ultils.AppUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

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
