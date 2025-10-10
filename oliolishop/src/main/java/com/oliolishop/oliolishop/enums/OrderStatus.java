package com.oliolishop.oliolishop.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PENDING_PAYMENT("pending_payment", "Chờ thanh toán"),
    PENDING_CONFIRMATION("pending_confirmation", "Chờ xác nhận"),
    CONFIRMED("confirmed", "Đã xác nhận"),
    READY_TO_PICK("ready_to_pick", "Chờ lấy hàng"),
    SHIPPING("shipping", "Đang giao hàng"),
    DELIVERED("delivered", "Đã giao hàng"),
    RETURNED("returned", "Trả hàng"),
    CANCELLED("cancelled", "Đã hủy"),
    PAYMENT_FAILED("payment_failed", "Thanh toán thất bại"),
    PARTIALLY_RETURNED("partially_returned", "Trả hàng một phần");

    private final String dbValue;
    private final String label;

    OrderStatus(String dbValue, String label) {
        this.dbValue = dbValue;
        this.label = label;
    }

    @JsonValue // Khi trả JSON → tiếng Việt
    public String getLabel() {
        return label;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static OrderStatus fromDbValue(String dbValue) {
        for (OrderStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown DB value: " + dbValue);
    }
}
