package com.oliolishop.oliolishop.dto.authenticate;

import com.oliolishop.oliolishop.validator.StrongPasswordConstraint;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuthenticateRequest {
    @Size(min = 5, message = "USERNAME_INVALID")
    String username;

    @StrongPasswordConstraint(message ="PASSWORD_INVALID")
    String password;
}
