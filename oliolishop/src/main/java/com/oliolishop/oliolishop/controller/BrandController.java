package com.oliolishop.oliolishop.controller;

import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.brand.BrandRequest;
import com.oliolishop.oliolishop.dto.brand.BrandResponse;
import com.oliolishop.oliolishop.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;


@RestController
@RequestMapping(ApiPath.brand.ROOT)
@Slf4j
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping
    public ApiResponse<?> getBrands(@RequestParam(name = "search", required = false) String searchKey,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size){
        return ApiResponse.builder()
                .result(brandService.getBrands(searchKey,page,size))
                .build();
    }


    @GetMapping(ApiPath.brand.GET_BY_CATEGORY)
    public ApiResponse<?> getBrandsByCategory(@RequestParam(name = "id") String categoryId,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){

        return ApiResponse.<PaginatedResponse<BrandResponse>>builder()
                .result(brandService.getBrandByCategory(categoryId,page,size))
                .build();
    }

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<BrandResponse> getBrand(@PathVariable("id") String id){
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.getBrandById(id))
                .build();
    }

    @PutMapping(ApiPath.BY_ID)
    public ApiResponse<BrandResponse> updateBrand(@PathVariable("id") String id,
                                                  @RequestBody BrandRequest request){
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.updateBrand(id,request))
                .build();
    }

    @PostMapping
    public ApiResponse<BrandResponse> createBrand(@RequestBody BrandRequest request){
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.createBrand(request))
                .build();
    }

    @DeleteMapping(ApiPath.BY_ID)
    public ApiResponse<Boolean> deleteBrand(@PathVariable("id")String id){
        brandService.deleteBrand(id);
        return  ApiResponse.<Boolean>builder()
                .result(true)
                .build();
    }
}