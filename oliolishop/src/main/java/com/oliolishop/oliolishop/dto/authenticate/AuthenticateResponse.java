package com.oliolishop.oliolishop.dto.authenticate;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuthenticateResponse {
    boolean authenticated;
    String accessToken;
    String refreshToken;
}
