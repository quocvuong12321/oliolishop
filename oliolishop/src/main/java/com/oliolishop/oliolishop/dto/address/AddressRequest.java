package com.oliolishop.oliolishop.dto.address;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {

    String customerId;     // hoặc sẽ lấy từ token nên có thể bỏ luôn
    String wardId;         // chỉ cần id của ward
    String detailAddress;
    String phoneNumber;
    String name;
    Boolean isDefault;

}
