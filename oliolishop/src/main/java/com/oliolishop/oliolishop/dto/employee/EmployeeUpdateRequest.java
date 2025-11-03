package com.oliolishop.oliolishop.dto.employee;

import com.oliolishop.oliolishop.validator.ValidationRegex;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeUpdateRequest {
    String name;
    @Pattern(regexp = ValidationRegex.VIETNAM_PHONE_REGEX, message = "PHONE_NUMBER_INVALID_FORMAT")
    @Size(min = 10, max = 11, message = "PHONE_NUMBER_INVALID_FORMAT")
    String phoneNumber;
    @Pattern(regexp = ValidationRegex.EMAIL_REGEX, message = "EMAIL_INVALID_FORMAT")
    String email;
    String password;
    String roleId;
}