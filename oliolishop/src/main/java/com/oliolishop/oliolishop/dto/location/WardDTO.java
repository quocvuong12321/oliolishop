package com.oliolishop.oliolishop.dto.location;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE )
@Builder
public class WardDTO {
    String id;
    String name;
    String districtId; // ID của huyện
}