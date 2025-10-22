package com.oliolishop.oliolishop.dto.ghn;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GhnPreviewResponse {
    DataResponse data;

    @Data
    public static class DataResponse {
        BigDecimal total_fee;
        String expected_delivery_time;
    }
}
