package com.oliolishop.oliolishop.controller;

import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.order.OrderRequest;
import com.oliolishop.oliolishop.dto.order.OrderResponse;
import com.oliolishop.oliolishop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

}
