package com.oliolishop.oliolishop.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_method")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PaymentMethod {
    @Id
    @Column(name = "payment_method_id")
    String id;

    String code;

    String description;

    String iconUrl;

    boolean active;

    @CreationTimestamp
    LocalDateTime createDate;

    @UpdateTimestamp
    LocalDateTime updateDate;

}
