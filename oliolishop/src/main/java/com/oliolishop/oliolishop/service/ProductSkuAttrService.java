package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.productskuattr.Request.ProductSkuAttrValueRequest;
import com.oliolishop.oliolishop.dto.productskuattr.Request.ProductSkuGenerateRequest;
import com.oliolishop.oliolishop.dto.productskuattr.Response.ProductSkuAttrCreateResponse;
import com.oliolishop.oliolishop.dto.productskuattr.Response.ProductSkuAttrValueResponse;
import com.oliolishop.oliolishop.entity.ProductSkuAttr;
import com.oliolishop.oliolishop.entity.ProductSpu;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.ProductSkuAttrMapper;
import com.oliolishop.oliolishop.repository.DescriptionAttrRepository;
import com.oliolishop.oliolishop.repository.ProductSkuAttrRepository;
import com.oliolishop.oliolishop.repository.ProductSpuRepository;
import com.oliolishop.oliolishop.ultils.AppUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductSkuAttrService {
    ProductSkuAttrRepository productSkuAttrRepository;
    ProductSkuAttrMapper productSkuAttrMapper;
    private final ProductSpuRepository productSpuRepository;
    private final DescriptionAttrRepository descriptionAttrRepository;

    @Transactional
    public ProductSkuAttrCreateResponse createAttributes(ProductSkuGenerateRequest request, List<MultipartFile> files,String imageDir,String folder) throws IOException {
        ProductSpu productSpu = productSpuRepository.findById(request.getProductSpuId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        Map<String, Set<ProductSkuAttrValueRequest>> attributes = request.getAttributes();



        ProductSkuAttrCreateResponse response = ProductSkuAttrCreateResponse.builder()
                .productSpuId(productSpu.getId())
                .name(productSpu.getName())
                .build();

        response.setAttributes(insertAttrs(productSpu, attributes,files,imageDir,folder));
        return response;
    }


    private Map<String, Set<ProductSkuAttrValueResponse>> insertAttrs(
            ProductSpu spu,
            Map<String, Set<ProductSkuAttrValueRequest>> attributes,
            List<MultipartFile> files,
            String imageDir,
            String folder) throws IOException {

        Map<String, Set<ProductSkuAttrValueResponse>> attributesResponse = new HashMap<>();
        List<ProductSkuAttr> datas = new ArrayList<>();

        AtomicInteger fileIndex = new AtomicInteger(0);

        for (String key : attributes.keySet()) {
            Set<ProductSkuAttrValueRequest> setAttr = attributes.get(key);
            Set<ProductSkuAttrValueResponse> setAttrResponse = new HashSet<>();
            for (ProductSkuAttrValueRequest attr : setAttr) {
                ProductSkuAttr data = productSkuAttrMapper.toProductSku(attr);
                data.setName(key);
                data.setId(UUID.randomUUID().toString());
                data.setValue(attr.getValue());
                data.setSpu(spu);
                data.setShowPreviewImage(attr.isShowPreviewImage());

                // Nếu có file & showPreviewImage=true → lưu ảnh
                if (attr.isShowPreviewImage() && attr.getFileIndex() != null && files != null && attr.getFileIndex() < files.size()) {
                    MultipartFile file = files.get(attr.getFileIndex());
                    String fileName = data.getId(); // tên file = id của attr
                    String imageUrl = AppUtils.saveImage(file, imageDir, folder, fileName);
                    attr.setImage(imageUrl);
                    data.setImage(imageUrl);
                } else {
                    data.setImage(attr.getImage()); // giữ nguyên nếu đã có
                }
                setAttrResponse.add(ProductSkuAttrValueResponse.builder()
                                .id(data.getId())
                                .image(data.getImage())
                                .value(data.getValue())
                                .showPreviewImage(data.getShowPreviewImage())
                        .build());
                datas.add(data);
            }

            attributesResponse.put(key, setAttrResponse);
        }

        productSkuAttrRepository.saveAll(datas);
        return attributesResponse;
    }
}
