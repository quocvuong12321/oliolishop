package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.productsku.ProductSkuResponse;
import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.repository.ProductSkuRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ProductSkuService {
    ProductSkuRepository productSkuRepository;

    public List<ProductSkuResponse> getSkus(String id){
        List<ProductSku> lst = productSkuRepository.findByProductSkuId(id);

        List<ProductSkuResponse> l = new ArrayList<>();
        lst.forEach(s -> l.add(ProductSkuResponse.builder()
                .id(s.getId())
                .skuCode(s.getSkuCode())
                .image(s.getImage())
                .product_spu_id(s.getSpu().getId())
                .originalPrice(s.getOriginalPrice())
                .sort(s.getSort())
                .build()
        ));
        return l;
    }
}
