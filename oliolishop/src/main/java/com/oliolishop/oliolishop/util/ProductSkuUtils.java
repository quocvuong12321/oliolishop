package com.oliolishop.oliolishop.util;

import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.entity.ProductSkuAttr;
import com.oliolishop.oliolishop.repository.ProductSkuAttrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductSkuUtils {

    private final ProductSkuAttrRepository productSkuAttrRepository;

    public String getVariant(ProductSku sku) {
        if (sku == null || sku.getSkuCode() == null) return "";

        String[] attrIds = sku.getSkuCode().split("/");

        List<ProductSkuAttr> attrs = productSkuAttrRepository.findAllById(Arrays.asList(attrIds));

        List<String> variants = Arrays.stream(attrIds)
                .map(id -> attrs.stream()
                        .filter(a -> a.getId().equals(id))
                        .findFirst()
                        .map(ProductSkuAttr::getValue)
                        .orElse("N/A"))
                .collect(Collectors.toList());

        return String.join(", ", variants);
    }

}
