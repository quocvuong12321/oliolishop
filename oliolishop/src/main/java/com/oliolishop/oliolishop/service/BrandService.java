package com.oliolishop.oliolishop.service;


import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.brand.BrandRequest;
import com.oliolishop.oliolishop.dto.brand.BrandResponse;
import com.oliolishop.oliolishop.entity.Brand;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.BrandMapper;
import com.oliolishop.oliolishop.repository.BrandRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandService {
    private BrandRepository brandRepository;
    private BrandMapper brandMapper;
    public PaginatedResponse<BrandResponse> getBrands(String searchKey, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BrandResponse> springPage;

        if (searchKey == null || searchKey.isBlank()) {
            // Trường hợp không có keyword, trả về tất cả
            springPage = brandRepository.findAll(pageable)
                    .map(brandMapper::toResponse);
        } else {
            // Trường hợp có keyword
            springPage = brandRepository.findByNameContainingIgnoreCase(searchKey.trim(), pageable)
                    .map(brandMapper::toResponse);
        }

        // Chuyển từ Page -> PaginatedResponse
        return PaginatedResponse.fromSpringPage(springPage);
    }

    public PaginatedResponse<BrandResponse> getBrandByCategory(String categoryId, int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Brand> brands = brandRepository.findDistinctBrandsByCategoryId(categoryId,pageable);
        return PaginatedResponse.fromSpringPage(brands.map(brandMapper::toResponse));
    }


    public BrandResponse getBrandById(String id){
        return brandMapper.toResponse(brandRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.BRAND_NOT_EXISTED)));
    }

    public BrandResponse updateBrand(String brandId, BrandRequest request){
        var brand = brandRepository.findById(brandId).orElseThrow(()->new AppException(ErrorCode.BRAND_NOT_EXISTED));
        brand.setName(request.getName());
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    public  BrandResponse createBrand(BrandRequest request){
        Brand brand =brandMapper.toBrand(request);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    public void deleteBrand(String id){
        Brand brand = brandRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.BRAND_NOT_EXISTED));

        brandRepository.deleteById(id);

    }


}
