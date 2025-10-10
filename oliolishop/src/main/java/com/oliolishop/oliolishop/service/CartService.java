package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.cart.CartItemRequest;
import com.oliolishop.oliolishop.dto.cart.CartItemResponse;
import com.oliolishop.oliolishop.dto.cart.CartResponse;
import com.oliolishop.oliolishop.entity.*;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.CartItemMapper;
import com.oliolishop.oliolishop.mapper.CartMapper;
import com.oliolishop.oliolishop.repository.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    CartItemMapper cartItemMapper;
    CartItemRepository cartItemRepository;
    ProductSkuService productSkuService;
    private final CartMapper cartMapper;
    private final ProductSkuAttrRepository productSkuAttrRepository;
    ProductSpuService productSpuService;
    private final ProductSkuRepository productSkuRepository;
    private final ProductSpuRepository productSpuRepository;

    public void createCart() {


        String customerId = getCustomerIdByJwt();

        Cart cart = Cart.builder()
                .customerId(customerId)
                .cartItems(new ArrayList<>())
                .build();
        cartRepository.save(cart);
    }

    public void addCartItem(CartItemRequest request) {

        String customerId = getCustomerIdByJwt();

        boolean existed = cartRepository.existsByCustomerId(customerId);
        if (!existed) {
            createCart();
        }

        Cart cart = cartRepository.findByCustomerId(customerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        ProductSku p = productSkuRepository.findById(request.getProductSkuId()).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        if (cart.getCartItems().stream().anyMatch(cartItem1 -> cartItem1.getProductSku().getId().equals(request.getProductSkuId()))) {
            CartItem hadItem = cartItemRepository.findByCartIdAndProductSkuId(cart.getId(), request.getProductSkuId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));
            int currentQuantity = hadItem.getQuantity();
            hadItem.setQuantity(currentQuantity + request.getQuantity());

            cartItemRepository.save(hadItem);
        } else {
            CartItem cartItem = cartItemMapper.toCartItem(request);
            cartItem.setThumbnail(p.getImage());
            cartItem.setVariant(getVariant(p));
            cartItem.setCart(cart);
            cartItem.setPrice(p.getOriginalPrice());
            cartItem.setProductSku(p);
            cartItemRepository.save(cartItem);
        }
    }

    //chỉ update số lượng
    public void updateCartItem(int num, Long cartItemId) {
        CartItem hadCartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        int currentQuantity = hadCartItem.getQuantity();

        int newQuantity = currentQuantity+num;
        if(newQuantity<=0)
            newQuantity=1;

        hadCartItem.setQuantity(newQuantity);

        cartItemRepository.save(hadCartItem);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem hadCartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        cartItemRepository.delete(hadCartItem);
    }

    public CartResponse getCart() {

        String customerId = getCustomerIdByJwt();
        Cart hadCart = cartRepository.findByCustomerId(customerId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        List<CartItem> cartItems = hadCart.getCartItems();

//        List<String> skuIds = cartItems.stream().map(item ->item.getProductSku().getId()).toList();

//        List<ProductSpu> spus = productSpuRepository.findAllBySkuIds(skuIds);
//
//        Map<String, ProductSpu> spuMap;
//        spus.forEach(spu->productSpuRepository.findBySkuId());


        List<CartItemResponse> cartItemResponses = new ArrayList<>();
        cartItems.forEach(item ->{
            CartItemResponse cartItemResponse = cartItemMapper.toResponse(item);
            ProductSpu spu = productSpuRepository.findBySkuId(item.getProductSku().getId());
            cartItemResponse.setName(spu.getName());
            cartItemResponse.setProductSpuId(spu.getId());
            cartItemResponses.add(cartItemResponse);
                }
        );

        return CartResponse.builder()
                .cartItems(cartItemResponses)
                .build();
    }

    protected static String getCustomerIdByJwt() {
        Authentication authentication = CustomerAuthenticationService.getAuthentication();


        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaim("customerId");
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    protected String getVariant(ProductSku sku) {

        String[] attrIds = sku.getSkuCode().split("/");

        // Lấy tất cả attributes trong 1 lần query
        List<ProductSkuAttr> attrs = productSkuAttrRepository.findAllById(Arrays.asList(attrIds));

        // Map theo đúng thứ tự attrIds
        List<String> variants = Arrays.stream(attrIds)
                .map(id -> attrs.stream()
                        .filter(a -> a.getId().equals(id))
                        .findFirst()
                        .map(ProductSkuAttr::getValue)
                        .orElse("N/A"))
                .toList();

        return String.join(", ", variants);
    }

    public void deleteCart(){
        String customerId = getCustomerIdByJwt();

        Cart c = cartRepository.findByCustomerId(customerId).orElseThrow(()->new AppException(ErrorCode.CART_NOT_EXISTED));

        cartRepository.delete(c);

    }

}
