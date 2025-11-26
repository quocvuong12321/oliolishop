package com.oliolishop.oliolishop.dto.permission;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionResponse {

    String id;
    String name;
    String description;
    LocalDateTime createDate;
    LocalDateTime updateDate;

}
