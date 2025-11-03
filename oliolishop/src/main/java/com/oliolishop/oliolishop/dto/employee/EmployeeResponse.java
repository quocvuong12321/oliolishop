package com.oliolishop.oliolishop.dto.employee;

import com.oliolishop.oliolishop.entity.Account;
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
    Account.AccountStatus status;

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
