package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.cart.CartItemRequest;
import com.oliolishop.oliolishop.dto.cart.CartItemResponse;
import com.oliolishop.oliolishop.entity.CartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface CartItemMapper {

    CartItem toCartItem(CartItemRequest request);

    CartItemResponse toResponse(CartItem cartItem);


}
