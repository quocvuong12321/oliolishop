package com.oliolishop.oliolishop.entity;

import com.oliolishop.oliolishop.ultils.AppUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_price",
       uniqueConstraints = {
           @UniqueConstraint(name = "idx_sku_primary", 
                           columnNames = {"product_sku_id", "is_primary", "status"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionPrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_price_id")
    private Long promotionPriceId;
    
    @Column(name = "product_sku_id", nullable = false, length = 36)
    private String productSkuId;
    
    @Column(name = "original_price", nullable = false)
    private double originalPrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_rule_id", nullable = false)
    private DiscountRule discountRule;
    
    @Column(name = "discounted_price", nullable = false)
    private double discountedPrice;
    
    @Column(name = "discount_amount", nullable = false)
    private double discountAmount;
    
    @Column(name = "start_time")
    @Builder.Default
    private LocalDateTime startTime = LocalDateTime.now();
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.ACTIVE;
    
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
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
    
    public double getDiscountPercent() {
        return AppUtils.round((discountedPrice/originalPrice)*100,2);
    }
}