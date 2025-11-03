package com.oliolishop.oliolishop.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employee_id")
    String id;

    String username;

    String password;

    String name;

    String phoneNumber;

    String email;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    @Enumerated(EnumType.STRING)
    Account.AccountStatus status;
}
