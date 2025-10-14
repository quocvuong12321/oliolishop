package com.oliolishop.oliolishop.controller;

import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.order.OrderRequest;
import com.oliolishop.oliolishop.dto.order.OrderResponse;
import com.oliolishop.oliolishop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(ApiPath.Order.ROOT)
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request){

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();

    }
    @PostMapping(ApiPath.Order.CONFIRM+ApiPath.BY_ID)
    public ApiResponse<String> confirmOrder(@PathVariable(name = "id") String orderId){
        orderService.confirmOrder(orderId);
        return ApiResponse.<String>builder()
                .result(MessageConstants.ORDER_CONFIRM_SUCCESSFULLY)
                .build();
    }

}
