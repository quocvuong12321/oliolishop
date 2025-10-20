package com.oliolishop.oliolishop.dto.location;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DistrictDTO {
    String id;
    String name;
    String provinceId; // ID của tỉnh để tiện liên kết
    // Không chứa List<Ward>
}