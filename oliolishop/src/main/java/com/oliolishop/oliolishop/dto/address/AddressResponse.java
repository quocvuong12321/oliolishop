package com.oliolishop.oliolishop.dto.address;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    String id;
    String name;
    String phoneNumber;
    String detailAddress;
    boolean isDefault;
}
