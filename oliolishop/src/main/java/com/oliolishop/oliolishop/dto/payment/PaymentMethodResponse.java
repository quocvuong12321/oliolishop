package com.oliolishop.oliolishop.dto.payment;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentMethodResponse {

    String id;
    String name;
    String description;
    String iconUrl;

}
