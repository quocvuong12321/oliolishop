package com.oliolishop.oliolishop.dto.account;


import com.oliolishop.oliolishop.entity.Customer;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountUpdateRequest {

    String phoneNumber;
    String name;
    LocalDate dob;
    Customer.Gender gender;

}
