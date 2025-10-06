package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "discount_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType = RuleType.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id", length = 36)
    private String targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType = DiscountType.PERCENT;

    @Column(name = "discount_value")
    private double discountValue;

    @Column(name = "min_discount")
    private double minDiscount;

    @Column(name = "max_discount")
    private double maxDiscount;

    @Column(name = "start_time")
    private LocalDateTime startTime = LocalDateTime.now();

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "priority")
    private Integer priority = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "allow_return", nullable = false)
    private Boolean allowReturn = true;

    public enum RuleType {
        NORMAL, DEFAULT
    }

    public enum TargetType {
        CATEGORY, BRAND, PRODUCT, ALL
    }

    public enum DiscountType {
        PERCENT, FIXED
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    // Helper methods
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public boolean isExpired() {
        return endTime != null && LocalDateTime.now().isAfter(endTime);
    }

    public boolean isValid() {
        return isActive() && !isExpired();
    }

}
