package com.oliolishop.oliolishop.dto.discount_rule;

import com.oliolishop.oliolishop.entity.DiscountRule;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRuleResponse {
    Long id;
    String name;
    DiscountRule.RuleType ruleType;
    DiscountRule.TargetType targetType;
    String targetId;
    DiscountRule.DiscountType discountType;
    double discountValue;
    double minDiscount;
    double maxDiscount;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Integer priority;
    DiscountRule.Status status;
    Boolean allowReturn;
}
