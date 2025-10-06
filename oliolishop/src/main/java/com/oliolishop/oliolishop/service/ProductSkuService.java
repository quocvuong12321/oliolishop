package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.productsku.ProductSkuDeleteRequest;
import com.oliolishop.oliolishop.dto.productsku.ProductSkuResponse;
import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.entity.ProductSkuAttr;
import com.oliolishop.oliolishop.entity.ProductSpu;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.ProductSkuMapper;
import com.oliolishop.oliolishop.repository.ProductSkuAttrRepository;
import com.oliolishop.oliolishop.repository.ProductSkuRepository;
import com.oliolishop.oliolishop.repository.ProductSpuRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ProductSkuService {
    ProductSkuRepository productSkuRepository;
    private final ProductSpuRepository productSpuRepository;
    private final ProductSkuAttrRepository productSkuAttrRepository;
    private final ProductSkuMapper productSkuMapper;

    public ProductSkuResponse getSkus(String id){
        ProductSku sku = productSkuRepository.findByProductSkuId(id).orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        return productSkuMapper.toResponse(sku);

    }

    @Transactional
    public void generateSkuForSpu(String spuId) {
        ProductSpu spu = productSpuRepository.findById(spuId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        // Lấy tất cả attributes
        List<ProductSkuAttr> attrs = productSkuAttrRepository.findBySpu_Id(spuId);

        // Nhóm theo name
        Map<String, List<ProductSkuAttr>> grouped = attrs.stream()
                .collect(Collectors.groupingBy(ProductSkuAttr::getName));

        // Lấy danh sách tập hợp giá trị
        List<List<ProductSkuAttr>> attrValues = new ArrayList<>(grouped.values());

        // Tạo tất cả tổ hợp
        List<List<ProductSkuAttr>> combinations = combine(attrValues);

        List<ProductSku> skus = new ArrayList<>();
        for (List<ProductSkuAttr> combo : combinations) {
            ProductSku sku = new ProductSku();
            sku.setId(UUID.randomUUID().toString());

            // Tạo sku_code dựa trên SPU + các giá trị attribute
            String skuCode = spu.getKey() + "/" + combo.stream()
                    .map(ProductSkuAttr::getValue)
                    .collect(Collectors.joining("/")).toUpperCase();
            sku.setSkuCode(skuCode);

            sku.setSpu(spu);
            sku.setOriginalPrice(0.0); // hoặc mặc định 0
            sku.setSkuStock(0);
            sku.setRestockStrategy(ProductSku.RestockStrategy.stop_ordering);
            sku.setStatus(ProductSku.Status.Inactive);
            sku.setWeight(0.0000001);

            // Chọn ảnh từ attribute nếu có, ưu tiên giá trị có showPreviewImage=true
            Optional<ProductSkuAttr> preview = combo.stream()
                    .filter(ProductSkuAttr::getShowPreviewImage)
                    .findFirst();
            sku.setImage(preview.map(ProductSkuAttr::getImage).orElse(spu.getImage()));

            skus.add(sku);
        }

        productSkuRepository.saveAll(skus);
    }

    // Hàm sinh tổ hợp n chiều
    private List<List<ProductSkuAttr>> combine(List<List<ProductSkuAttr>> lists) {
        List<List<ProductSkuAttr>> resultLists = new ArrayList<>();
        if (lists.isEmpty()) {
            resultLists.add(new ArrayList<>());
            return resultLists;
        } else {
            List<ProductSkuAttr> firstList = lists.get(0);
            List<List<ProductSkuAttr>> remainingLists = combine(lists.subList(1, lists.size()));
            for (ProductSkuAttr condition : firstList) {
                for (List<ProductSkuAttr> remaining : remainingLists) {
                    List<ProductSkuAttr> resultList = new ArrayList<>();
                    resultList.add(condition);
                    resultList.addAll(remaining);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }

    public Boolean deleteSku(ProductSkuDeleteRequest request) //inActive nó đi
    {
        List<ProductSku> lst = productSkuRepository.findAllById(request.getLstSkuId());


        if(!lst.isEmpty()){
            for(var item:lst){
                item.setStatus(ProductSku.Status.Inactive);
            }
            productSkuRepository.saveAll(lst);
            return true;
        }
        else throw new AppException(ErrorCode.EMPTY_PRODUCT_SKU);

    }
}
