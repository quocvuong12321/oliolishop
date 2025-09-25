package com.oliolishop.oliolishop.dto.productskuattr.Request;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSkuAttrValueRequest {
    @NotNull(message = "VALUE_REQUIRED")
    String value;
    String image;
    boolean showPreviewImage;
    Integer fileIndex;
}
