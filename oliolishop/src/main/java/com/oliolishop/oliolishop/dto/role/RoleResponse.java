package com.oliolishop.oliolishop.dto.role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String roleId;
    String name;
    String description;

}
