package com.oliolishop.oliolishop.dto.account;


import com.oliolishop.oliolishop.dto.customer.CustomerRequest;
import com.oliolishop.oliolishop.enums.Role;
import com.oliolishop.oliolishop.validator.StrongPasswordConstraint;
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
    @StrongPasswordConstraint(message ="PASSWORD_INVALID")
    String password;
    CustomerRequest customerRequest;
    String phoneNumber;
}
