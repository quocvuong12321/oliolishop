package com.oliolishop.oliolishop.dto.order;

import com.oliolishop.oliolishop.enums.OrderStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSearchCriteria {
    // 1. Lọc theo trạng thái
    List<OrderStatus> statuses;

    // 2. Lọc theo thời gian tạo
    LocalDateTime startDate;
    LocalDateTime endDate;

    // 3. Lọc theo thông tin người nhận
    String receiverPhone;
    String receiverName;

    // 4. Lọc theo giá trị đơn hàng
    BigDecimal minFinalAmount;
    BigDecimal maxFinalAmount;

    // 5. Lọc theo Mã/ID
    String voucherCode;
    String orderId;

    // 6. Lọc theo người xác nhận (Employee)
    String confirmedByEmployeeId;
}