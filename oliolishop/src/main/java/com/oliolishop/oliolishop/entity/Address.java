package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "address")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {

    @Id
    @Column(name = "address_id")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    Ward ward;

    String detailAddress;

    String phoneNumber;

    String name;

    @Column(name = "is_default")
    boolean idDefault;

    @CreationTimestamp
    LocalDateTime createDate;

    @UpdateTimestamp
    LocalDateTime updateDate;

}
