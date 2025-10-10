package com.oliolishop.oliolishop.dto.authenticate;

import com.oliolishop.oliolishop.dto.account.AccountRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    String otp;
    AccountRequest accountRequest;
}
