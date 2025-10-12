package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.cart.CartItemResponse;
import com.oliolishop.oliolishop.dto.order.OrderItemRequest;
import com.oliolishop.oliolishop.dto.order.OrderItemResponse;
import com.oliolishop.oliolishop.dto.order.OrderRequest;
import com.oliolishop.oliolishop.dto.order.OrderResponse;
import com.oliolishop.oliolishop.entity.*;
import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.OrderItemMapper;
import com.oliolishop.oliolishop.mapper.OrderMapper;
import com.oliolishop.oliolishop.repository.*;
import com.oliolishop.oliolishop.util.AppUtils;
import com.oliolishop.oliolishop.util.ProductSkuUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class OrderService {
    OrderItemRepository orderItemRepository;
    OrderRepository orderRepository;
    OrderItemMapper orderItemMapper;
    CustomerRepository customerRepository;
    AddressRepository addressRepository;
    VoucherRepository voucherRepository;
    OrderMapper orderMapper;
    ProductSkuRepository productSkuRepository;
    ProductSpuRepository productSpuRepository;
    ProductSkuUtils productSkuUtils;
    @Transactional
    public OrderResponse createOrder(OrderRequest request){

        String customerId = AppUtils.getCustomerIdByJwt();
        Customer customer = customerRepository.findById(customerId).orElseThrow(()->new AppException(ErrorCode.CUSTOMER_NOT_EXISTED));

        Address address = addressRepository.findById(request.getAddressId()).orElseThrow(()-> new AppException(ErrorCode.ADDRESS_NOT_EXIST));

        Voucher voucher = null;
        if(!request.getVoucherCode().isEmpty()){
            voucher = voucherRepository.findByVoucherCode(request.getVoucherCode()).orElseThrow(()->new AppException(ErrorCode.VOUCHER_NOT_EXISTED));
        }

        Order order = orderMapper.toOrder(request);
        order.setId(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setAddress(address);
        order.setVoucher(voucher);
        order.setFeeShip(request.getFeeShip());
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        order.setShippingAddress(request.getShippingAddress());
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for(OrderItemRequest item : request.getOrderItems()){
            ProductSku sku = productSkuRepository.findById(item.getProductSkuId()).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_EXIST));

            if(sku.getSkuStock() < item.getQuantity())
                throw new AppException(ErrorCode.NOT_ENOUGH_STOCK);

            sku.setSkuStock(sku.getSkuStock() - item.getQuantity());

            OrderItem orderItem  = orderItemMapper.toOrderItem(item);

            orderItem.setOrder(order);

            orderItem.setUnitPrice(sku.getOriginalPrice());

            orderItem.setId(UUID.randomUUID().toString());

            orderItem.setReturnQuantity(0);

            orderItem.setAllowReturn(true);

            orderItem.setProductSku(sku);

            totalAmount =  totalAmount.add(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        BigDecimal voucherDiscount = BigDecimal.ZERO;
        if(voucher != null &&
                totalAmount.compareTo(BigDecimal.valueOf(voucher.getMinOrderValue())) > 0){
            order.setVoucherCode(voucher.getVoucherCode());

            BigDecimal percentDiscount = BigDecimal
                    .valueOf(voucher.getDiscountPercent())
                    .multiply(totalAmount)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal maxDiscount = BigDecimal.valueOf(voucher.getMaxDiscountValue());

            voucherDiscount = percentDiscount.min(maxDiscount);

            voucher.setAmount(voucher.getAmount()-1);
        }

        order.setVoucherDiscountAmount(voucherDiscount);

        BigDecimal finalAmount = totalAmount
                .add(request.getFeeShip())
                .subtract(voucherDiscount)
                .setScale(2,RoundingMode.HALF_UP)
                ;

        order.setFinalAmount(finalAmount);

        BigDecimal loyalPoint = finalAmount.divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP);

        customer.setLoyaltyPoints(customer.getLoyaltyPoints().add(loyalPoint));

        OrderResponse response = orderMapper.toResponse(orderRepository.save(order));
        response.setId(order.getId());
        response.setStatus(order.getOrderStatus());
        response.setCreateDate(order.getCreateDate());



        List<OrderItemResponse> orderItemResponses = new ArrayList<>();

        orderItems.forEach(orderItem -> {
            OrderItemResponse itemResponse = orderItemMapper.toResponse(orderItem);
            ProductSku sku = orderItem.getProductSku();
            ProductSpu spu = productSpuRepository.findBySkuId(sku.getId());
            itemResponse.setId(orderItem.getId());
            itemResponse.setProductSkuId(sku.getId());
            itemResponse.setName(spu.getName());
            itemResponse.setThumbnail(spu.getImage());
            itemResponse.setVariant(productSkuUtils.getVariant(sku));
            orderItemResponses.add(itemResponse);
       });

       response.setOrderItems(orderItemResponses);

       return response;
    }

}
