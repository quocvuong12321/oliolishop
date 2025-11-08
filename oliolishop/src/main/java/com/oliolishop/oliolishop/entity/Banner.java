package com.oliolishop.oliolishop.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Banner {
    @Id
    @Column(name = "banner_id", length = 36)
    String id;

    String name;
    String content;
    String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

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


}
