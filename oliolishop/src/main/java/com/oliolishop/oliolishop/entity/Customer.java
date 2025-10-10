package com.oliolishop.oliolishop.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Customer {
    @Id
    @Column(name = "customer_id")
    String id;

    @Column(name = "name")
    String name;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "customer",orphanRemoval = true,cascade = CascadeType.ALL)
    List<Address> addresses;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "customer")
    List<Order> orders;

    @Column(name = "image")
    String image;

    @Column(name = "dob")
    LocalDate dob;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    Gender gender;

    int loyaltyPoints;

    @Column(name = "create_date")
    LocalDateTime createDate;

    @Column(name = "update_date")
    LocalDateTime updateDate;


    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Account_id")
    Account account;


    public enum Gender {
        Nam, Nữ
    }
}
