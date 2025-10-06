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
public class DiscountRuleRequest {
    private String name;
    private DiscountRule.RuleType ruleType;
    private DiscountRule.TargetType targetType;
    private String targetId;
    private DiscountRule.DiscountType discountType;
    private double discountValue;
    private double minDiscount;
    private double maxDiscount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer priority;
    private DiscountRule.Status status;
    private Boolean allowReturn;
}
