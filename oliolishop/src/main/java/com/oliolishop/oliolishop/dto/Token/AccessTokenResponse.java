package com.oliolishop.oliolishop.dto.Token;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessTokenResponse {
    String accessToken;
    String role;
    Set<String> permissions;

}
