package com.oliolishop.oliolishop.dto.Token;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessTokenResponse {
    String accessToken;
}
