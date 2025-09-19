package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.mapper.ProductSkuAttrMapper;
import com.oliolishop.oliolishop.repository.ProductSkuAttrRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductSkuAttrService {
    ProductSkuAttrRepository productSkuAttrRepository;
    ProductSkuAttrMapper productSkuAttrMapper;

}
