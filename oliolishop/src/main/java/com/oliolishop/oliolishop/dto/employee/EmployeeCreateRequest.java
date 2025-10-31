package com.oliolishop.oliolishop.dto.employee;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeCreateRequest {
    String name;
    String phoneNumber;
    String email;
    String roleId;
}
