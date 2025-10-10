package com.oliolishop.oliolishop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "province")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Province {

    @Id
    @Column(name = "province_id", length = 20)
    String id;

    @Column(name = "name", nullable = false, length = 100)
    String name;

    Integer countryId;
    String provinceEncode;
    Integer regionId;
    Integer areaId;
    Boolean canUpdateCod; // tinyint(1)
    Boolean status;

    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<District> districts;
}