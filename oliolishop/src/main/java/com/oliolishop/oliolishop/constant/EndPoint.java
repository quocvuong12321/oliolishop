package com.oliolishop.oliolishop.constant;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EndPoint {
    public static final String API_PREFIX = "/api";

    public static final String[] GET_PUBLIC={
            "/brand/**",
            "/category",
            "/spu/**",
            "/image/**",
            "/sku-attr/**",
            "/sku/**",
            "/order/order-statuses",
            "/voucher/**",
            "/banner/**",
            "/payment/vnpay/return",
            "/locations/**"
    };

    public static final String[] POST_PUBLIC = {
            "/auth",
            "/auth/refresh",
            "/auth/reset-password",
            "/auth/send-otp",
            "/auth/verify-otp",
            "/auth/register/**",
            "/employee/login",
            "/employee/refresh"
    };

    public static String[] prefix(String[] arr, String prefix){
        return Arrays.stream(arr)
                .map(s->prefix+s)
                .toArray(String[]::new);
    }
}
