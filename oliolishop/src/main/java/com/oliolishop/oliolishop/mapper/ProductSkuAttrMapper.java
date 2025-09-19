package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.productskuattr.ProductSkuAttrResponse;
import com.oliolishop.oliolishop.entity.ProductSkuAttr;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface ProductSkuAttrMapper {

    ProductSkuAttrResponse toResponse(ProductSkuAttr productSkuAttr);

}
