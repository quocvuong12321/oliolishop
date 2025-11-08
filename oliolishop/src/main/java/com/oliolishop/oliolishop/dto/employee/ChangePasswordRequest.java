package com.oliolishop.oliolishop.dto.employee;

import com.oliolishop.oliolishop.validator.StrongPasswordConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @NotBlank(message = "VALUE_REQUIRED")
    String oldPassword;
    @NotBlank
    @StrongPasswordConstraint(message = "PASSWORD_INVALID")
    String newPassword;
}