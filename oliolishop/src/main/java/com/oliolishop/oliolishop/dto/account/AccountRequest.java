package com.oliolishop.oliolishop.dto.account;


import com.oliolishop.oliolishop.validator.StrongPasswordConstraint;
import com.oliolishop.oliolishop.validator.ValidationRegex;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountRequest {
    @Size(min = 5, message = "USERNAME_INVALID")
    String username;
    String email;
    @StrongPasswordConstraint(message = "PASSWORD_INVALID")
    String password;
    @Pattern(regexp = ValidationRegex.VIETNAM_PHONE_REGEX, message = "PHONE_NUMBER_INVALID_FORMAT")
    @Size(min = 10, max = 11, message = "PHONE_NUMBER_INVALID_FORMAT")
    String phoneNumber;
}
