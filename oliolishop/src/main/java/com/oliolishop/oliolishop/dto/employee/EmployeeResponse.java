package com.oliolishop.oliolishop.dto.employee;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeResponse {

    String id;
    String username;
    String name;
    String phoneNumber;
    String email;
    RoleResponse role;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RoleResponse{
        String id;
        String name;
        String permission;
    }
}
