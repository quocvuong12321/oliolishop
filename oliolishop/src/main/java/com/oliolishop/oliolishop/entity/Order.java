package com.oliolishop.oliolishop.entity;


import jakarta.persistence.*;

@Entity
@Table(name="order")
public class Order {
    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Customer customer;



}
