package com.oliolishop.oliolishop.dto.productspu;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.oliolishop.oliolishop.dto.brand.BrandResponse;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrResponse;
import com.oliolishop.oliolishop.entity.DescriptionAttr;
import com.oliolishop.oliolishop.ultils.AppUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpuCreateResponse {
    String id;
    String name;
    String image;
    String shortDescription;
    String description;
    String key;
    CategoryResponse category;
    BrandResponse brand;
    List<DescriptionAttrResponse> descriptionAttrs;
    String[] media;
    int sold;
    String stockStatus;
    String deleteStatus;
    LocalDateTime createDate;
    LocalDateTime updateDate;

    public void setMedia(String mediaStr) throws JsonProcessingException {
        this.media = AppUtils.parseStringToArray(mediaStr);
    }

    public void setMedia(String[] media){
        this.media=media;
    }
}
