package com.oliolishop.oliolishop.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CancelOrderRequest {
    @NotNull(message = "INVALID_KEY")
    String orderId;

    // Cần paymentMethodId để tạo bản ghi transaction REFUND
    @NotNull(message = "INVALID_KEY")
    String paymentMethodId;
}
