package com.oliolishop.oliolishop.dto.banner;


import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerResponse {
    String id;
    String name;
    String content;
    String image;
    CategoryResponse category;
    LocalDateTime createDate;
}
