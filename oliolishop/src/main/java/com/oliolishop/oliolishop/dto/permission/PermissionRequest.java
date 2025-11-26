package com.oliolishop.oliolishop.dto.permission;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {

    String id;
    String name;
    String permission;

}
