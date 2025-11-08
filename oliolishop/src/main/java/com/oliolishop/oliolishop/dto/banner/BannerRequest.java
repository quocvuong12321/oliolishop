package com.oliolishop.oliolishop.dto.banner;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerRequest {
    String name;
    String content;
    String categoryId;
}
