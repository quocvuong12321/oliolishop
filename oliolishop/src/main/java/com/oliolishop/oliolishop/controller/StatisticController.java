package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.statistic.DailyRevenueResponse;
import com.oliolishop.oliolishop.dto.statistic.RevenueStatisticsResponse;
import com.oliolishop.oliolishop.entity.Order;
import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.repository.OrderRepository;
import com.oliolishop.oliolishop.service.StatisticService;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(ApiPath.Statistic.ROOT)
public class StatisticController {
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping(ApiPath.Statistic.TOTAL)
    public ApiResponse<RevenueStatisticsResponse> getTotalRevenue() {
        return ApiResponse.<RevenueStatisticsResponse>builder()
                .result(statisticService.getTotalRevenue())
                .build();
    }

    @GetMapping(ApiPath.Statistic.DAILY)
    public ApiResponse<List<DailyRevenueResponse>> getDailyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ApiResponse.<List<DailyRevenueResponse>>builder()
                .result(statisticService.getDailyRevenue(start, end))
                .build();
    }

    @GetMapping(ApiPath.Statistic.MONTH)
    public ApiResponse<RevenueStatisticsResponse> getMonthRevenue(
            @RequestParam int year,
            @RequestParam int month) {
        return ApiResponse.<RevenueStatisticsResponse>builder()
                .result(statisticService.getMonthlyRevenue(year, month))
                .build();
    }

    @GetMapping(ApiPath.Statistic.QUARTER)
    public ApiResponse<RevenueStatisticsResponse> getQuarterRevenue(
            @RequestParam int year,
            @RequestParam int quarter) {
        return ApiResponse.<RevenueStatisticsResponse>builder()
                .result(statisticService.getQuarterlyRevenue(year, quarter))
                .build();
    }

    @GetMapping(ApiPath.Statistic.YEAR)
    public ApiResponse<RevenueStatisticsResponse> getYearRevenue(
            @RequestParam int year
            ) {
        return ApiResponse.<RevenueStatisticsResponse>builder()
                .result(statisticService.getYearlyRevenue(year))
                .build();
    }


}



