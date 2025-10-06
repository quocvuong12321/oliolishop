package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.cart.CartItemRequest;
import com.oliolishop.oliolishop.dto.cart.CartResponse;
import com.oliolishop.oliolishop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.BASE+ApiPath.CART)
public class CartController {
    @Autowired
    private CartService  cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart(){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart())
                .build();
    }


    @PostMapping
    public ApiResponse<String> addCartItem(@RequestBody CartItemRequest request){
        cartService.addCartItem(request);

        return ApiResponse.<String>builder()
                .result(MessageConstants.ADD_TO_CART)
                .build();
    }

    @PutMapping
    public ApiResponse<Void> updateCartItem(@RequestParam long cartItemId, @RequestParam int number){
        cartService.updateCartItem(number,cartItemId);

        return ApiResponse.<Void>builder().build();
    }

    @DeleteMapping("/item")
    public ApiResponse<String> deleteCartItem(@RequestParam long cartItemId){
        cartService.deleteCartItem(cartItemId);
        return ApiResponse.<String>builder().build();
    }

    @DeleteMapping
    public ApiResponse<String> deleteCart(){
        cartService.deleteCart();
        return ApiResponse.<String>builder()
                .result(MessageConstants.ORDER_SUCCESSFUL).build();
    }

}
