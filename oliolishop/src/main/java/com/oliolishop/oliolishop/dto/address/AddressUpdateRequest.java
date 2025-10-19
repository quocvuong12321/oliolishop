package com.oliolishop.oliolishop.dto.address;

import com.oliolishop.oliolishop.validator.ValidationRegex;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressUpdateRequest {

    String wardId;         // chỉ cần id của ward
    String detailAddress;
    @Pattern(regexp = ValidationRegex.VIETNAM_PHONE_REGEX, message = "PHONE_NUMBER_INVALID_FORMAT")
    @Size(min = 10, max = 11, message = "PHONE_NUMBER_INVALID_FORMAT")
    String phoneNumber;
    String name;
    Boolean isDefault;
}
