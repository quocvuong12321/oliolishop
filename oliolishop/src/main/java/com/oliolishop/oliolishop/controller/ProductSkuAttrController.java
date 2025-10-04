package com.oliolishop.oliolishop.controller;

import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.productskuattr.Request.ProductSkuGenerateRequest;
import com.oliolishop.oliolishop.dto.productskuattr.Response.ProductSkuAttrCreateResponse;
import com.oliolishop.oliolishop.service.ProductSkuAttrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RestController
@RequestMapping(ApiPath.BASE + ApiPath.SKU_ATTR)
public class ProductSkuAttrController {
    @Autowired
    private ProductSkuAttrService productSkuAttrService;
    @Value("${app.image-dir}")
    private String imageDir; // D:/HocTap/AI/crawl/images

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> createSkuAttr(
            @RequestPart(value = "request") ProductSkuGenerateRequest request,
            @RequestPart(value = "files",required = false) List<MultipartFile> files
    ) throws IOException {
        String folder = ApiPath.FOLDER_IMAGE_ATTR;
        return  ApiResponse.<ProductSkuAttrCreateResponse>builder()
                .result(productSkuAttrService.createAttributes(request,files,imageDir,folder))
                .build();
    }
}
