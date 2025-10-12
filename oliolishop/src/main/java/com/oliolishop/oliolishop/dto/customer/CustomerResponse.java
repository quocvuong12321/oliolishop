package com.oliolishop.oliolishop.dto.customer;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponse {
    String customerId;
    String name;
    LocalDate dob;
    String gender;
    BigDecimal loyaltyPoints;
    String image;
}
