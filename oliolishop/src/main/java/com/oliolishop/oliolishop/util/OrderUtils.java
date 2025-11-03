package com.oliolishop.oliolishop.util;

import com.oliolishop.oliolishop.dto.order.OrderSearchCriteria;
import com.oliolishop.oliolishop.entity.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
public class OrderUtils {
    public static Specification<Order> byCriteria(OrderSearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Đảm bảo không lấy các trường bị lặp khi JOIN (tránh Distinct)
            query.distinct(true);

            // 1. Lọc theo Trạng thái (orderStatus)
            if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
                predicates.add(root.get("orderStatus").in(criteria.getStatuses()));
            }

            // 2. Lọc theo Khoảng Ngày tạo (createDate)
            if (criteria.getStartDate() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("createDate"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                // Thêm 1 ngày để đảm bảo bao gồm cả ngày cuối cùng
                predicates.add(builder.lessThanOrEqualTo(root.get("createDate"), criteria.getEndDate()));
            }

            // 3. Lọc theo Số điện thoại người nhận (receiverPhone - tìm chính xác)
            if (StringUtils.hasText(criteria.getReceiverPhone())) {
                predicates.add(builder.equal(root.get("receiverPhone"), criteria.getReceiverPhone()));
            }

            // 4. Lọc theo Tên người nhận (receiverName - tìm kiếm LIKE)
            if (StringUtils.hasText(criteria.getReceiverName())) {
                String likePattern = "%" + criteria.getReceiverName().toLowerCase() + "%";
                predicates.add(builder.like(builder.lower(root.get("receiverName")), likePattern));
            }

            // 5. Lọc theo Khoảng Tổng tiền cuối cùng (finalAmount)
            if (criteria.getMinFinalAmount() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("finalAmount"), criteria.getMinFinalAmount()));
            }
            if (criteria.getMaxFinalAmount() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("finalAmount"), criteria.getMaxFinalAmount()));
            }

            // 6. Lọc theo Mã Voucher (voucherCode)
            if (StringUtils.hasText(criteria.getVoucherCode())) {
                predicates.add(builder.equal(root.get("voucherCode"), criteria.getVoucherCode()));
            }

            // 7. Lọc theo ID Đơn hàng (orderId)
            if (StringUtils.hasText(criteria.getOrderId())) {
                predicates.add(builder.equal(root.get("id"), criteria.getOrderId()));
            }

            // 8. Lọc theo người xác nhận (confirmBy) - JOIN
            if (criteria.getConfirmedByEmployeeId() != null) {
                predicates.add(builder.equal(root.join("confirmBy").get("id"), criteria.getConfirmedByEmployeeId()));
            }

            // Kết hợp tất cả các điều kiện (Predicate) bằng toán tử AND
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
