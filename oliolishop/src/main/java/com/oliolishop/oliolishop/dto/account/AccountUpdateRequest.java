package com.oliolishop.oliolishop.dto.account;


import com.oliolishop.oliolishop.entity.Customer;
import com.oliolishop.oliolishop.validator.DobConstraint;
import com.oliolishop.oliolishop.validator.ValidationRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountUpdateRequest {

    @NotBlank
    String name;
    @DobConstraint(message = "DOB_INVALID",min = 16)
    LocalDate dob;
    @Pattern(regexp = ValidationRegex.VIETNAM_PHONE_REGEX, message = "PHONE_NUMBER_INVALID_FORMAT")
    @Size(min = 10, max = 11, message = "PHONE_NUMBER_INVALID_FORMAT")
    String phoneNumber;
    Customer.Gender gender;

}
