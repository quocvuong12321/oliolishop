package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuResponse;
import com.oliolishop.oliolishop.entity.ProductSpu;
import com.oliolishop.oliolishop.mapper.ProductSpuMapper;
import com.oliolishop.oliolishop.repository.ProductSpuRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpuService {
    ProductSpuRepository productSpuRepository;
    ProductSpuMapper productSpuMapper;
    public Page<ProductSpuResponse> getProducts(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productSpuRepository.findProducts(pageable).map(p -> ProductSpuResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .price(p.getProductSkus().getFirst().getPrice())
                .image(p.getImage())
                .sold(p.getSold())
                .originalPrice(p.getProductSkus().getFirst().getOriginalPrice())
                .discountRate((int)(p.getProductSkus().getFirst().getDiscountRate()*100))
                .build());
    }

    public Page<ProductSpuResponse> getProductsByCategory(String categoryId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return productSpuRepository.findByCategory(categoryId,pageable).map(p->ProductSpuResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .originalPrice(p.getProductSkus().getFirst().getOriginalPrice())
                .price(p.getProductSkus().getFirst().getPrice())
                .discountRate((int)(p.getProductSkus().getFirst().getDiscountRate()*100))
                .sold(p.getSold())
                .image(p.getImage())
                .build());
    }
}
