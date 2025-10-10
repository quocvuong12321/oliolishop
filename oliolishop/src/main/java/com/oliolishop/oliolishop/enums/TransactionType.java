package com.oliolishop.oliolishop.enums;

import lombok.Getter;

@Getter
public enum TransactionType  {
    PAYMENT("Thanh toán"),
    REFUND("Hoàn tiền"),
    CANCEL("Hủy");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

}
