package com.oliolishop.oliolishop.dto.statistic;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueStatisticsResponse {
    private BigDecimal totalRevenue; // tổng doanh thu
    private BigDecimal netRevenue;   // doanh thu thuần (trừ voucher/discount)
    private long totalOrders;        // tổng số đơn đã giao
}