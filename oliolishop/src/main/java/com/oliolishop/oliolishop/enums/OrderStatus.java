package com.oliolishop.oliolishop.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
        pending_payment("Chờ thanh toán"),
        pending_confirmation("Chờ xác nhận"),
        confirmed("Đã xác nhận"),
        ready_to_pick("Chờ lấy hàng"),
        shipping("Chờ giao hàng"),
        delivered("Đã giao"),
        returned("Trả hàng"),
        pending_cancellation("Chờ hủy đơn"),
        cancelled("Đã hủy"),
        payment_failed("Thanh toán thất bại"),
        partially_returned("Trả hàng một phần");

        private final String label;

        OrderStatus(String label) {
            this.label = label;
        }

}
