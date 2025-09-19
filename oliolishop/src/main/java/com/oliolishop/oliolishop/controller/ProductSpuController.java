package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductDetailResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuCreateRequest;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuResponse;
import com.oliolishop.oliolishop.dto.productspu.SpuCreateResponse;
import com.oliolishop.oliolishop.repository.ProductSpuRepository;
import com.oliolishop.oliolishop.service.ProductSpuService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RestController
@RequestMapping(ApiPath.BASE+"/spu")
public class ProductSpuController {
    @Autowired
    private  ProductSpuService productSpuService;
    @Autowired
    private  ProductSpuRepository productSpuRepository;
    @Value("${app.image-dir}")
    private  String imageDir;

    @GetMapping
    public ApiResponse<?> getProductsSpu(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "30")int size){
        Page<ProductSpuResponse> pages = productSpuService.getProducts(page,size);

        return ApiResponse.builder()
                .result(new PaginatedResponse<>(
                        pages.getContent(),
                        pages.getNumber(),
                        pages.getSize(),
                        pages.getTotalElements(),
                        pages.getTotalPages()
                ))
                .build();

    }

    @GetMapping("/{category_id}")
    public ApiResponse<?> getProductsSpuByCategory(
            @PathVariable("category_id") String categoryId,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "30")int size){
        Page<ProductSpuResponse> pages = productSpuService.getProductsByCategory(categoryId,page,size);

        return ApiResponse.builder()
                .result(new PaginatedResponse<>(
                        pages.getContent(),
                        pages.getNumber(),
                        pages.getSize(),
                        pages.getTotalElements(),
                        pages.getTotalPages()
                ))
                .build();
    }

    @GetMapping("/detail"+ApiPath.BY_ID)
    public ApiResponse<ProductDetailResponse> detailProduct(@PathVariable String id){

        return ApiResponse.<ProductDetailResponse>builder()
                .result(productSpuService.detailProduct(id))
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SpuCreateResponse> createProduct(
            @RequestPart("product") ProductSpuCreateRequest request,
            @RequestPart("files") List<MultipartFile> files) throws IOException {
        System.out.println(request);
        files.forEach(f -> System.out.println(f.getOriginalFilename()));
        return ApiResponse.<SpuCreateResponse>builder()
                .result(productSpuService.createProductSpu(request,files,imageDir))
                .build();
    }

    @PostMapping(value = "/test",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void testUpload(@RequestPart("product") String productJson,
                           @RequestPart("files") List<MultipartFile> files) {
        System.out.println(productJson);
        files.forEach(f -> System.out.println(f.getOriginalFilename()));
    }

}
