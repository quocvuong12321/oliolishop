package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.productspu.ProductSpuCreateRequest;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuCreateResponse;
import com.oliolishop.oliolishop.entity.ProductSpu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface ProductSpuMapper {
//
//    @Mapping(target = "price", ignore = true)
//    @Mapping(target = "originalPrice", ignore = true)
//    @Mapping(target = "discountRate", ignore = true)
//    ProductSpuResponse toResponse(ProductSpu spu);


//    // ---- custom helpers ----
//    default double getFirstPrice(ProductSpu spu) {
////        return spu.getProductSkus().isEmpty() ? 0.0 : spu.getProductSkus().getFirst().getPrice();
//        return spu.getProductSkus().isEmpty() ? 0.0 : spu.getProductSkus().stream().toList().getFirst().getPrice();
//    }
//
//    default double getFirstOriginalPrice(ProductSpu spu) {
//        return spu.getProductSkus().isEmpty() ? 0.0:spu.getProductSkus().stream().toList().getFirst().getOriginalPrice();
//    }
//
//    default int getFirstDiscountRate(ProductSpu spu) {
//        if (spu.getProductSkus().isEmpty()) return 0;
//        return (int) (spu.getProductSkus().getFirst().getDiscountRate() * 100);
//    }


    @Mapping(target = "media",ignore = true)
    ProductSpuCreateResponse toSpuCreateResponse(ProductSpu spu);
    ProductSpu toProductSpu(ProductSpuCreateRequest request);


}
