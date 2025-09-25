package com.oliolishop.oliolishop.dto.category;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryAllResponse {
    CategoryResponse category;
    Set<CategoryResponse> children;
}
