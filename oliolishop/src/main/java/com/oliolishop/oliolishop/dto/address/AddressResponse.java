package com.oliolishop.oliolishop.dto.address;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    String addressId;
    String name;
    String phoneNumber;
    String detailAddress;
    String wardName;   // Lấy từ join
    String districtName;
    String provinceName;
    Boolean isDefault;
}
