package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.productsku.ProductUpdateRequest;
import com.oliolishop.oliolishop.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@RestController
@RequestMapping(ApiPath.ProductSku.ROOT)
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

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<?> findSkuBySpuId(@PathVariable("id") String spuId){
        return ApiResponse.builder()
                .result(productSkuService.getProductSkuBySpuId(spuId))
                .build();
    }

    @PutMapping(ApiPath.BY_ID)
    public ApiResponse<?> updateSku(@PathVariable("id")String skuId,@RequestBody ProductUpdateRequest request){

        return ApiResponse.builder()
                .result(productSkuService.updateStockPriceWeight(request,skuId))
                .build();
    }
}
