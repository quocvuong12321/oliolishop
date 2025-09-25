package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.productskuattr.Request.ProductSkuAttrValueRequest;
import com.oliolishop.oliolishop.dto.productskuattr.ProductSkuAttrResponse;
import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.entity.ProductSkuAttr;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface ProductSkuAttrMapper {

    ProductSkuAttrResponse toResponse(ProductSkuAttr productSkuAttr);

    @Mapping(ignore = true,target = "name")
    ProductSkuAttr toProductSku(ProductSkuAttrValueRequest request);

    ProductSkuAttrValueRequest toCreateResponse(ProductSkuAttr productSkuAttr);
}
