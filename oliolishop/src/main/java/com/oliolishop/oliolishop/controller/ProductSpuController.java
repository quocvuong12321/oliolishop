package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductDetailResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuCreateRequest;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuCreateResponse;
import com.oliolishop.oliolishop.dto.rating.RatingResponse;
import com.oliolishop.oliolishop.repository.ProductSpuRepository;
import com.oliolishop.oliolishop.service.ProductSpuService;
import com.oliolishop.oliolishop.service.RatingService;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RestController
@RequestMapping(ApiPath.Spu.ROOT)
public class ProductSpuController {
    @Autowired
    private ProductSpuService productSpuService;
    @Autowired
    private RatingService ratingService;
    @Value("${app.image-dir}")
    private String imageDir;


    @GetMapping
    public ApiResponse<?> getProductsSpu(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "30") int size, @RequestParam(required = false) String categoryId, @RequestParam(defaultValue = "0") double minPrice, @RequestParam(defaultValue = "99999999") double maxPrice, @RequestParam(required = false) String brandId) {
        List<ProductSpuResponse> pages = productSpuService.getProducts(categoryId, brandId, minPrice, maxPrice, page, size);
        int totalElements = productSpuService.getTotalElements(categoryId, brandId, minPrice, maxPrice);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return ApiResponse.builder()
                .result(
                        PaginatedResponse.<ProductSpuResponse>builder()
                                .content(pages)
                                .totalPages(totalPages)
                                .page(page)
                                .size(size)
                                .totalElements(totalElements)
                                .hasPreviousPage(page>0)
                                .hasNextPage(page<totalPages-1)
                                .build()
                )
                .build();

    }

    @GetMapping(ApiPath.Spu.DETAIL)
    public ApiResponse<ProductDetailResponse> detailProduct(@PathVariable(name ="id") String id) {

        return ApiResponse.<ProductDetailResponse>builder()
                .result(productSpuService.detailProduct(id))
                .build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductSpuCreateResponse> createProduct(
            @RequestPart("product") ProductSpuCreateRequest request,
            @RequestPart("files") List<MultipartFile> files) throws IOException {
        System.out.println(request);
        files.forEach(f -> System.out.println(f.getOriginalFilename()));
        return ApiResponse.<ProductSpuCreateResponse>builder()
                .result(productSpuService.createProductSpu(request, files, imageDir))
                .build();
    }

    @PutMapping(value = ApiPath.BY_ID)
    public ApiResponse<ProductSpuCreateResponse> updateProduct(
            @PathVariable String id,
            @RequestBody ProductSpuCreateRequest request)
    {
        return ApiResponse.<ProductSpuCreateResponse>builder()
                .result(productSpuService.updateProductSpu(request,id))
                .build();
    }

    @GetMapping(ApiPath.Spu.RATING)
    public ApiResponse<List<RatingResponse>> getProductRatings(
            @PathVariable(value = "id") String spuId,
            @RequestParam(defaultValue = "0") int page, // Tham số phân trang (page)
            @RequestParam(defaultValue = "10") int size // Tham số phân trang (size)
    ){
        // Gọi RatingService để lấy danh sách đã phân trang
        List<RatingResponse> ratings = ratingService.getRatingForDetailProduct(spuId, page, size);

        return ApiResponse.<List<RatingResponse>>builder()
                .result(ratings)
                .build();
    }

    @PostMapping(ApiPath.Spu.LIKE_RATING)
    public ApiResponse<String> likeRating(@RequestParam(value = "ratingId") String ratingId){
        ratingService.likeRating(ratingId);
        return ApiResponse.<String>builder()
                .result("")
                .build();
    }

    @DeleteMapping(ApiPath.Spu.LIKE_RATING)
    public ApiResponse<String> cancelLikeRating(@RequestParam(value = "ratingId") String ratingId){
        ratingService.cancelLikeRating(ratingId);
        return ApiResponse.<String>builder()
                .result("")
                .build();
    }
}
