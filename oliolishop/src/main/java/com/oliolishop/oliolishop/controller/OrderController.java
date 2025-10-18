package com.oliolishop.oliolishop.controller;

import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.order.OrderRequest;
import com.oliolishop.oliolishop.dto.order.OrderResponse;
import com.oliolishop.oliolishop.dto.rating.RatingRequest;
import com.oliolishop.oliolishop.dto.rating.RatingResponse;
import com.oliolishop.oliolishop.service.OrderService;
import com.oliolishop.oliolishop.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(ApiPath.Order.ROOT)
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    RatingService ratingService;
    @Value("${app.image-dir}")
    private String imageDir; // D:/HocTap/AI/crawl/images

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

    @PostMapping(ApiPath.Order.RATING)
    public ApiResponse<RatingResponse> createOrder(@RequestPart(value = "request") RatingRequest request,
                                                   @RequestPart(value = "files",required = false) List<MultipartFile> files) throws IOException {

        RatingResponse response = ratingService.createRating(request,imageDir,ApiPath.FOLDER_IMAGE_RATING,files);
        return ApiResponse.<RatingResponse>builder()
                .result(response)
                .build();
    }

}
