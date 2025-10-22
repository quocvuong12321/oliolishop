package com.oliolishop.oliolishop.controller;

import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.order.*;
import com.oliolishop.oliolishop.dto.rating.RatingRequest;
import com.oliolishop.oliolishop.dto.rating.RatingResponse;
import com.oliolishop.oliolishop.service.OrderService;
import com.oliolishop.oliolishop.service.RatingService;
import jakarta.validation.Valid;
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

    @PostMapping(ApiPath.Order.CANCEL_ORDER)
    public ApiResponse<String> cancelOrder(@Valid @RequestBody CancelOrderRequest request){
        int result = orderService.cancelOrder(request.getOrderId(), request.getPaymentMethodId());

        String message = switch (result) {
            case 1 -> "Yêu cầu hủy đơn hàng đã được ghi nhận, chờ Admin xử lý hoặc hoàn tiền.";
            case 2 -> "Hoàn tiền đơn hàng thành công và đơn đã được hủy.";
            case 3 -> "Hủy đơn hàng thất bại hoặc hoàn tiền không thành công. Vui lòng liên hệ bộ phận hỗ trợ.";
            default -> "Yêu cầu hủy đơn hàng không thể thực hiện.";
        };

        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }

    @PostMapping(ApiPath.Order.CHECK_OUT)
    public ApiResponse<CheckOutResponse> checkOut(@RequestBody CheckOutRequest request){

        return ApiResponse.<CheckOutResponse>builder()
                .result(orderService.checkOut(request))
                .build();
    }

}
