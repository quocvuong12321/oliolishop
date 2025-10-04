package com.oliolishop.oliolishop.dto.authenticate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordResponse {
    String accessToken;
    String refreshToken;
}