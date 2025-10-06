package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.cart.CartRequest;
import com.oliolishop.oliolishop.dto.cart.CartResponse;
import com.oliolishop.oliolishop.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface CartMapper {
    CartResponse toResponse(Cart cart);

}
