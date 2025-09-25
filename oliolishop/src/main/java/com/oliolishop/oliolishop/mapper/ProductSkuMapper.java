package com.oliolishop.oliolishop.mapper;

import com.oliolishop.oliolishop.dto.productsku.ProductSkuResponse;
import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.entity.ProductSpu;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface ProductSkuMapper {

    ProductSkuResponse toResponse(ProductSku productSku);
}
