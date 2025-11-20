package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.configuration.CheckPermission;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.constant.MessageConstants;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.api.PaginatedResponse;
import com.oliolishop.oliolishop.dto.banner.BannerRequest;
import com.oliolishop.oliolishop.dto.banner.BannerResponse;
import com.oliolishop.oliolishop.service.BannerService;
import org.hibernate.query.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(ApiPath.Banner.ROOT)
public class BannerController {
    @Value("${app.image-dir}")
    private String imageDir;
    @Autowired
    BannerService bannerService;

    @CheckPermission("BANNER_CREATE")
    @PostMapping
    public ApiResponse<BannerResponse> createBanner(@RequestPart BannerRequest request, @RequestPart MultipartFile file){
        return ApiResponse.<BannerResponse>builder()
                .result(bannerService.createBanner(request,file,imageDir,ApiPath.FOLDER_IMAGE_BANNER))
                .build();
    }


    @GetMapping
    public ApiResponse<PaginatedResponse<BannerResponse>> getBanner(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name,
            @RequestParam (required = false)String categoryId
    ){
        return ApiResponse.<PaginatedResponse<BannerResponse>>builder()
                .result(bannerService.getBanners(name,categoryId,page,size))
                .build();
    }

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<BannerResponse> getBannerById(@PathVariable String id){
        return ApiResponse.<BannerResponse>builder()
                .result(bannerService.getBannerById(id))
                .build();
    }

    @CheckPermission("BANNER_DELETE")
    @DeleteMapping(ApiPath.BY_ID)
    public ApiResponse<String> deleteBannerById(@PathVariable String id){
        bannerService.deleteBanner(id,imageDir);
        return ApiResponse.<String>builder()
                .result(String.format(MessageConstants.SUCCESS,"Xóa banner"))
                .build();
    }

    @CheckPermission("BANNER_UPDATE")
    @PutMapping(ApiPath.BY_ID)
    public ApiResponse<BannerResponse> updateBanner(@PathVariable String id,
                                                    @RequestPart BannerRequest request,
                                                    @RequestPart(required = false) MultipartFile file
                                                    ){

        return ApiResponse.<BannerResponse>builder()
                .result(bannerService.updateBanner(id,request,file,imageDir,ApiPath.FOLDER_IMAGE_BANNER))
                .build();

    }


}
