package com.oliolishop.oliolishop.dto.ghn;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GhnPreviewRequest {
    String client_order_code;
//    String from_ward_name;
//    String from_district_name;
//    String from_province_name;
    String from_address;
    String to_name;
    String to_phone;
    String to_ward_code;
    String to_address;
    @Builder.Default
    int service_type_id = 2;
    @Builder.Default
    String required_note = "KHONGCHOXEMHANG";
    @Builder.Default
    int payment_type_id = 1;
    int weight;
    List<GhnItem> items;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GhnItem {
        String name;
        int quantity;
        int weight;
    }
}
