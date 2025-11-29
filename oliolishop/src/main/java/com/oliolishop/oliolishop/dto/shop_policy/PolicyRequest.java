package com.oliolishop.oliolishop.dto.shop_policy;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PolicyRequest {

    String item;
    String name;


}
