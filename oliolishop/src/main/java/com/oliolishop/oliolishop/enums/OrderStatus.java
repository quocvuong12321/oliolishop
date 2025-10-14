package com.oliolishop.oliolishop.enums;

import com.fasterxml.jackson.annotation.JsonValue;

    public enum OrderStatus {
        pending_payment,
        pending_confirmation,
        confirmed,
        ready_to_pick,
        shipping,
        delivered,
        returned,
        cancelled,
        payment_failed,
        partially_returned;
    }
