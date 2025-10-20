package com.oliolishop.oliolishop.dto.location;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProvinceDTO {
    String id;
    String name;
}