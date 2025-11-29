package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Getter
@Setter
@Table(name = "shop_policy_pdf")
public class ShopPolicyPdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String item;        // Ví dụ: "return_policy", "privacy_policy"

    String pdfPath;

    String name;

    @UpdateTimestamp
    Timestamp lastUpdate;
}
