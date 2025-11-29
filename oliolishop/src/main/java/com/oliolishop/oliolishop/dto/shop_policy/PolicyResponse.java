package com.oliolishop.oliolishop.dto.shop_policy;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PolicyResponse {

    Long id;
    String item;
    String name;


}
