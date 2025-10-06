package com.oliolishop.oliolishop.controller;


import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.discount_rule.DiscountRuleRequest;
import com.oliolishop.oliolishop.dto.discount_rule.DiscountRuleResponse;
import com.oliolishop.oliolishop.service.DiscountRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(ApiPath.BASE+ApiPath.DISCOUNT_RULE)
public class DiscountRuleController {

    @Autowired
    DiscountRuleService discountRuleService;

    @GetMapping
    public ApiResponse<Page<DiscountRuleResponse>> getDiscountRules(@RequestParam int page, @RequestParam int size){
        return ApiResponse.<Page<DiscountRuleResponse>>builder()
                .result(discountRuleService.getDiscountRule(page, size))
                .build();
    }

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<DiscountRuleResponse> getDiscountRule(@RequestParam String id){
        return ApiResponse.<DiscountRuleResponse>builder()
                .result(discountRuleService.getDiscountRuleById(id))
                .build();
    }

    @PostMapping
    public ApiResponse<DiscountRuleResponse> createDiscountRule(@RequestBody DiscountRuleRequest request){

        return ApiResponse.<DiscountRuleResponse>builder()
                .result(discountRuleService.createDiscountRule(request))
                .build();
    }

}
