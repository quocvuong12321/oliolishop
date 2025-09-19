package com.oliolishop.oliolishop.dto.productspu;


import com.oliolishop.oliolishop.dto.BreadCrumbResponse;
import com.oliolishop.oliolishop.dto.brand.BrandResponse;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.dto.descriptionattr.DescriptionAttrResponse;
import com.oliolishop.oliolishop.dto.productsku.ProductSkuResponse;
import com.oliolishop.oliolishop.dto.productskuattr.ProductSkuAttrResponse;
import com.oliolishop.oliolishop.ultils.AppUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
    String id;
    String name;
    int sold;
//    double rating;
//    int numRating;
    BrandResponse brand;
    List<ProductSpuResponse> productsSameCategory;
    List<ProductSpuResponse> productSameBrand;
    List<BreadCrumbResponse> breadCrumb;
    String shortDescription;
    String description;
    double originalPrice;
    double Price;
    double discountRate;
    Set<ProductSkuResponse> skus;
    Set<ProductSkuAttrResponse> skuAttrs;
    Set<DescriptionAttrResponse> desAttrs;
    String[] media;

    public void setMedia(String mediaStr) {
        try {
            this.media = AppUtils.parseStringToArray(mediaStr);
        } catch (Exception e) {
            e.printStackTrace();
            this.media = new String[0];
        }
    }
}
