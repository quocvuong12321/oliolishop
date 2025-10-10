package com.oliolishop.oliolishop.dto.authenticate;


import com.oliolishop.oliolishop.validator.StrongPasswordConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgetPasswordRequest {
    String otp;
    @StrongPasswordConstraint(message ="PASSWORD_INVALID")
    String newPassword;
    @StrongPasswordConstraint(message ="PASSWORD_INVALID")
    String reNewPassword;
    String email;
}
