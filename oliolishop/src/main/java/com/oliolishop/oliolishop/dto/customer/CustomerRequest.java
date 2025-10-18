package com.oliolishop.oliolishop.dto.customer;


import com.oliolishop.oliolishop.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerRequest {
    String name;
    @DobConstraint(message = "DOB_INVALID",min = 16)
    LocalDate dob;
    String gender;
}
