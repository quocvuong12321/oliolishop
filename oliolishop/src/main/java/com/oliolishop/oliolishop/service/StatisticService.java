package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.statistic.DailyRevenueResponse;
import com.oliolishop.oliolishop.dto.statistic.RevenueStatisticsResponse;
import com.oliolishop.oliolishop.entity.Order;
import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class StatisticService {

    OrderRepository orderRepository;

    public RevenueStatisticsResponse getTotalRevenue() {
        List<Order> deliveredOrders = orderRepository.findByOrderStatus(OrderStatus.delivered);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal netRevenue = BigDecimal.ZERO;

        for (Order order : deliveredOrders) {
            totalRevenue = totalRevenue.add(order.getFinalAmount().add(order.getVoucherDiscountAmount()));
            netRevenue = netRevenue.add(order.getFinalAmount());
        }

        return RevenueStatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .netRevenue(netRevenue)
                .totalOrders(deliveredOrders.size())
                .build();
    }

    public List<DailyRevenueResponse> getDailyRevenue(LocalDate startDate, LocalDate endDate) {
        List<DailyRevenueResponse> result = new ArrayList<>();
        LocalDate current = startDate;

        if(startDate.isAfter(endDate))
            throw new AppException(ErrorCode.START_DATE_MUST_BEFORE);

        while (!current.isAfter(endDate)) {
            LocalDateTime startOfDay = current.atStartOfDay();
            LocalDateTime endOfDay = current.plusDays(1).atStartOfDay();

            List<Order> orders = orderRepository.findByOrderStatusAndCreateDateBetween(
                    OrderStatus.delivered,
                    startOfDay,
                    endOfDay
            );

            BigDecimal dailyRevenue = orders.stream()
                    .map(Order::getFinalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            result.add(DailyRevenueResponse.builder()
                    .date(current)
                    .revenue(dailyRevenue)
                    .build()
            );

            current = current.plusDays(1);
        }

        return result;
    }
    public RevenueStatisticsResponse getMonthlyRevenue(int year, int month) {
        LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1);
        return calculateRevenue(start, end);
    }

    /**
     * Doanh thu theo quý
     */
    public RevenueStatisticsResponse getQuarterlyRevenue(int year, int quarter) {
        if (quarter < 1 || quarter > 4) throw new AppException(ErrorCode.QUARTER_INVALID );
        int startMonth = (quarter - 1) * 3 + 1;
        LocalDateTime start = LocalDate.of(year, startMonth, 1).atStartOfDay();
        LocalDateTime end = start.plusMonths(3);
        return calculateRevenue(start, end);
    }

    /**
     * Doanh thu theo năm
     */
    public RevenueStatisticsResponse getYearlyRevenue(int year) {
        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime end = start.plusYears(1);
        return calculateRevenue(start, end);
    }

    private RevenueStatisticsResponse calculateRevenue(LocalDateTime start,LocalDateTime end){
        if(start.isAfter(end))
            throw new AppException(ErrorCode.START_DATE_MUST_BEFORE);

        List<Order> orders=orderRepository.findByOrderStatusAndCreateDateBetween(OrderStatus.delivered,start,end);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal netRevenue = BigDecimal.ZERO;

        for(var order:orders){
            totalRevenue = totalRevenue.add(order.getTotalAmount().add(order.getFeeShip()));

            netRevenue = netRevenue.add(order.getFinalAmount());
        }
        return RevenueStatisticsResponse.builder()
                .netRevenue(netRevenue)
                .totalOrders(orders.size())
                .totalRevenue(totalRevenue)
                .build();
    }
}
