package com.oliolishop.oliolishop.dto.descriptionattr;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DescriptionAttrResponse {
    String id;
    String name;
    String value;
}
