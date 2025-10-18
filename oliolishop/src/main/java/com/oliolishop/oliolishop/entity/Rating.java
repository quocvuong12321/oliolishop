package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"customer_id", "order_item_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rating {

    @Id
    @Column(name = "rating_id", length = 36)
    String id; // UUID

    @Lob // Dùng cho trường TEXT
    @Column(name = "comment", columnDefinition = "TEXT")
    String comment;

    @Column(name = "images", columnDefinition = "TEXT")
    String images; // Lưu dưới dạng chuỗi JSON hoặc Python list string

    @Column(name = "star", nullable = false)
    Double star;

    // --- Mối quan hệ ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    OrderItem orderItem; // Đảm bảo mối quan hệ 1:1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_spu_id", nullable = false)
    ProductSpu productSpu;

    // --- Trường thuộc tính ---

    @ColumnDefault("0")
    @Column(name = "is_hidden", nullable = false)
    Boolean isHidden = false;

    @Lob
    @Column(name = "shop_reply", columnDefinition = "TEXT")
    String shopReply;

    @Column(name = "reply_date")
    LocalDateTime replyDate;

    @ColumnDefault("0")
    @Column(name = "likes_count", nullable = false)
    Integer likesCount = 0;

    // --- Audit Fields ---

    @CreationTimestamp
    @Column(name = "create_date", updatable = false)
    LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    LocalDateTime updateDate;

    // --- Mối quan hệ 1:N với RatingLike ---
    @OneToMany(mappedBy = "rating", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<RatingLike> likes;
}