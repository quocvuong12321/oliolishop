package com.oliolishop.oliolishop.dto.authenticate;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgetPasswordRequest {
    String otp;
    String newPassword;
    String reNewPassword;
    String email;
}
