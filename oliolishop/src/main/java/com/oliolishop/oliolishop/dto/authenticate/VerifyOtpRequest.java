package com.oliolishop.oliolishop.dto.authenticate;

import com.oliolishop.oliolishop.enums.OtpType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VerifyOtpRequest {
    String email;
    String otp;
    OtpType type;
}