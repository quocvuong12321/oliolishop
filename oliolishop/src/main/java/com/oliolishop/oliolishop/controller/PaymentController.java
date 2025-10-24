package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.service.OrderService;
import com.oliolishop.oliolishop.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;

@RestController
@RequestMapping(ApiPath.Payment.ROOT)
public class PaymentController {
    @Autowired
    OrderService orderService;
    @Autowired
    VNPayService vnPayService;

    private static final String FRONTEND_SUCCESS_URL = "http://localhost:4200/checkout/confirm";
    private static final String FRONTEND_FAIL_URL = "http://localhost:4200/payment/vnpay/failure";

    @GetMapping(ApiPath.Payment.VNPAY)
    public ApiResponse<?> createVnPayPayment(@RequestParam String orderId,@RequestParam String paymentMethodId){

        return ApiResponse.builder()
                .result(orderService.createVnPayPayment(orderId,paymentMethodId))
                .build();
    }

    @GetMapping(ApiPath.Payment.VNPAY_RETURN)
    public RedirectView paymentReturn(HttpServletRequest request) {
//        int result = vnPayService.orderReturn(request);
//        orderService.updateStatusTransaction(request,result);

        int result = orderService.updateStatusTransaction(request);
        String orderId = getOrderIdFromRequest(request);
        return switch (result) {
            case 1 ->
//                    ApiResponse.<String>builder().result(MessageConstants.PAYMENT_SUCCESS).build();
            new RedirectView(FRONTEND_SUCCESS_URL+"?orderId="+orderId);
            case 0 ->new RedirectView(FRONTEND_FAIL_URL+"?orderId="+orderId);
            default -> throw new AppException(ErrorCode.PAYMENT_INVALID);
        };
    }

    // Helper method
    private String getOrderIdFromRequest(HttpServletRequest request) {
        // Giả sử tham số VNPAY trả về có tên là 'vnp_TxnRef' chứa Order ID
        return request.getParameter("vnp_TxnRef");
    }
}
