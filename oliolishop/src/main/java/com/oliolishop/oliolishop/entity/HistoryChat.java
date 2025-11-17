package com.oliolishop.oliolishop.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistoryChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String sessionId;


    String customerId;

    String message;

    @Enumerated(EnumType.STRING)
    RoleChat role;

    public enum RoleChat {
        user, assistant
    }


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)  // tên cột trong DB
    LocalDateTime createdAt;

//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//    }

}
