package com.oliolishop.oliolishop.dto.productspu;

import com.oliolishop.oliolishop.dto.brand.BrandRequest;
import com.oliolishop.oliolishop.dto.category.CategoryRequest;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpuCreateRequest {
    String name;
    String description;
    String shortDescription;
    String brandId;
    String categoryId;
    List<DescriptionAttrRequest> descriptionAttrRequests;
}
