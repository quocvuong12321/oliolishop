package com.oliolishop.oliolishop.enums;

public enum OrderStatus {
    PENDING_PAYMENT("Chờ thanh toán"),
    PENDING_CONFIRMATION("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận"),
    WAITING_FOR_PICKUP("Chờ lấy hàng"),
    DELIVERING("Đang giao hàng"),
    DELIVERED("Đã giao hàng"),
    RETURNED("Trả hàng"),
    CANCELLED("Đã hủy"),
    PAYMENT_FAILED("Thanh toán thất bại"),
    PARTIAL_RETURN("Trả hàng một phần");

    private final String vietnameseName;

    OrderStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getValue() {
        return this.vietnameseName;
    }
    
    // Get bằng tên tiếng Việt
    public static OrderStatus fromValue(String vietnameseName) {
        for (OrderStatus status : values()) {
            if (status.vietnameseName.equals(vietnameseName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái: " + vietnameseName);
    }
}