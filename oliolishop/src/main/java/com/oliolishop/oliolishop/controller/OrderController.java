package com.oliolishop.oliolishop.controller;

import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.ghn.GhnPreviewResponse;
import com.oliolishop.oliolishop.dto.order.*;
import com.oliolishop.oliolishop.dto.rating.RatingRequest;
import com.oliolishop.oliolishop.dto.rating.RatingResponse;
import com.oliolishop.oliolishop.enums.OrderStatus;
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
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();

    }

    @PutMapping(ApiPath.Order.CONFIRM + ApiPath.BY_ID)
    public ApiResponse<String> confirmOrder(@PathVariable(name = "id") String orderId) {
        orderService.confirmOrder(orderId);
        return ApiResponse.<String>builder()
                .result(MessageConstants.ORDER_CONFIRM_SUCCESSFULLY)
                .build();
    }

    @PostMapping(ApiPath.Order.CREATE_SHIPPING)
    public ApiResponse<GhnPreviewResponse> createShipping(@RequestParam(name = "orderId")String orderId){

        return ApiResponse.<GhnPreviewResponse>builder()
                .result(orderService.createShipment(orderId))
                .build();

    }

    @PutMapping(ApiPath.Order.CONFIRM+ApiPath.BY_ID+ApiPath.Order.CANCEL_ORDER)
    public ApiResponse<String> confirmCancelOrder(@PathVariable(name = "id")String orderId){

        int result = orderService.confirmCancelOrder(orderId);

        String message = switch (result){
            case 0 ->MessageConstants.Cancel.PENDING_REFUND;
            case 1 -> MessageConstants.Cancel.PENDING;
            case 2 -> MessageConstants.Cancel.SUCCESS;
            case 3 -> MessageConstants.Cancel.FAIL;
            default -> MessageConstants.Cancel.CANT;
        };
        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<OrderResponse> getOrderById(@PathVariable(name = "id") String orderId) {

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId))
                .build();
    }

    @GetMapping
    public ApiResponse<PaginatedResponse<OrderResponse>> getOrderByCustomerId(@RequestParam(name = "status") OrderStatus status,
                                                                              @RequestParam(name = "page", defaultValue = "0") int page,
                                                                              @RequestParam(name = "size", defaultValue = "10") int size
    ) {

        return ApiResponse.<PaginatedResponse<OrderResponse>>builder()
                .result(
                        orderService.getOrdersByCustomerId(status, page, size))
                .build();

    }

    @GetMapping(ApiPath.Order.STATUS)
    public ApiResponse<PaginatedResponse<OrderResponse>> getOrderByStatus(
            @RequestParam(name = "status") OrderStatus status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ApiResponse.<PaginatedResponse<OrderResponse>>builder()
                .result(orderService.getOrderByStatus(status, page, size))
                .build();
    }

    @PostMapping(ApiPath.Order.RATING)
    public ApiResponse<RatingResponse> createOrder(@RequestPart(value = "request") RatingRequest request,
                                                   @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        RatingResponse response = ratingService.createRating(request, imageDir, ApiPath.FOLDER_IMAGE_RATING, files);
        return ApiResponse.<RatingResponse>builder()
                .result(response)
                .build();
    }

    @PostMapping(ApiPath.Order.CANCEL_ORDER)
    public ApiResponse<String> cancelOrder(@Valid @RequestBody CancelOrderRequest request) {
        int result = orderService.cancelOrder(request.getOrderId(), request.getPaymentMethodId());

        String message = switch (result) {
            case 0 ->MessageConstants.Cancel.PENDING_REFUND;
            case 1 -> MessageConstants.Cancel.PENDING;
            case 2 -> MessageConstants.Cancel.SUCCESS;
            case 3 -> MessageConstants.Cancel.FAIL;
            default -> MessageConstants.Cancel.CANT;
        };

        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }

    @PostMapping(ApiPath.Order.CHECK_OUT)
    public ApiResponse<CheckOutResponse> checkOut(@RequestBody CheckOutRequest request) {

        return ApiResponse.<CheckOutResponse>builder()
                .result(orderService.checkOut(request))
                .build();
    }

}
