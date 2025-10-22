package com.oliolishop.oliolishop.dto.address;


import com.oliolishop.oliolishop.dto.location.DistrictDTO;
import com.oliolishop.oliolishop.dto.location.ProvinceDTO;
import com.oliolishop.oliolishop.dto.location.WardDTO;
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
    boolean defaultAddress;
    ProvinceDTO province;
    DistrictDTO district;
    WardDTO ward;

}
