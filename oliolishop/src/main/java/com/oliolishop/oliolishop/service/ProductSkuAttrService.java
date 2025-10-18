package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.productskuattr.ProductSkuAttrResponse;
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
import com.oliolishop.oliolishop.util.AppUtils;
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

        // 1. Lặp qua các thuộc tính
        for (String key : attributes.keySet()) {
            Set<ProductSkuAttrValueRequest> setAttr = attributes.get(key);
            Set<ProductSkuAttrValueResponse> setAttrResponse = new HashSet<>();

            for (ProductSkuAttrValueRequest attr : setAttr) {
                ProductSkuAttr data = productSkuAttrMapper.toProductSku(attr);
                // Thiết lập các trường cơ bản
                data.setName(key);
                data.setId(UUID.randomUUID().toString());
                data.setSpu(spu);

                // 2. Xử lý logic lưu ảnh thuộc tính
                if (attr.isShowPreviewImage() && attr.getFileIndex() != null && files != null) {

                    int fileIndex = attr.getFileIndex();
                    // Đảm bảo chỉ mục hợp lệ và file không rỗng
                    if (fileIndex < files.size() && !files.get(fileIndex).isEmpty()) {
                        MultipartFile file = files.get(fileIndex);

                        // Tên file cơ sở là ID của thuộc tính SKU (đã là unique)
                        String fileNameBase = data.getId();

                        // GỌI HÀM LƯU ẢNH CHUẨN
                        String imageUrl = AppUtils.saveImage(file, imageDir, folder, fileNameBase);

                        data.setImage(imageUrl); // Cập nhật URL mới sau khi lưu
                    } else {
                        // Nếu index không hợp lệ hoặc file rỗng nhưng vẫn showPreviewImage=true
                        // Cần xử lý: có thể giữ nguyên URL ảnh cũ nếu có (attr.getImage()) hoặc set null
                        data.setImage(attr.getImage()); // Giữ nguyên ảnh cũ (hoặc null)
                    }
                } else {
                    // Nếu không showPreviewImage hoặc không upload file mới, giữ nguyên URL ảnh cũ
                    data.setImage(attr.getImage());
                }

                // 3. Chuẩn bị Response và Save list
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

        productSkuAttrRepository.saveAll(datas); // Lưu tất cả các thuộc tính SKU vào DB
        return attributesResponse;
    }

    public List<ProductSkuAttrResponse> findSkuAttrBySpuId(String spuId){
        ProductSpu spu = productSpuRepository.findById(spuId).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXIST));

        List<ProductSkuAttr> attrs =spu.getSkuAttrs().stream().toList();

        return attrs.stream().map(productSkuAttrMapper::toResponse).toList();
    }
}
