package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuResponse;
import com.oliolishop.oliolishop.service.ProductSpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPath.BASE+"/spu")
public class ProductSpuController {
    @Autowired
    ProductSpuService productSpuService;

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
}
