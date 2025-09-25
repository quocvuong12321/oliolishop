package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Component
@RestController
@RequestMapping(ApiPath.BASE + ApiPath.SKU)
public class ProductSkuController {
    @Autowired
    ProductSkuService productSkuService;

    @PostMapping(ApiPath.BY_ID)
    public ApiResponse<Void> createSkus(@PathVariable("id") String spuId){

        productSkuService.generateSkuForSpu(spuId);

        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }

}
