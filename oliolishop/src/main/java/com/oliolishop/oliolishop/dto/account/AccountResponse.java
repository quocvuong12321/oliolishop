package com.oliolishop.oliolishop.dto.account;

import com.oliolishop.oliolishop.dto.customer.CustomerRequest;
import com.oliolishop.oliolishop.dto.customer.CustomerResponse;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountResponse {
    String username;
    String email;
    String phoneNumber;
    CustomerResponse customerResponse;
}
