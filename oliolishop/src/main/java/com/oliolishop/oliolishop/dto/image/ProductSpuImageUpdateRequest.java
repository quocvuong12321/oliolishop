package com.oliolishop.oliolishop.dto.image;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpuImageUpdateRequest {
    List<String> existingImages;
    int thumbnailIndex;
}
