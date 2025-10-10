package com.oliolishop.oliolishop.dto.employee;


import com.oliolishop.oliolishop.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeRequest {

    String username;
    String password;
    String name;
    String phoneNumber;
    String email;
    String roleId;
}
