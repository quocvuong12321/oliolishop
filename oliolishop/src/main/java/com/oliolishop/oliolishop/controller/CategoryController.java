package com.oliolishop.oliolishop.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.category.CategoryRequest;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE + "/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategories() throws JsonProcessingException {
        log.info("you are in controller ");
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.loadParent())
                .build();
    }

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<List<CategoryResponse>> getCategoryById(@PathVariable(name = "id") String id) {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.findChildren(id))
                .build();
    }

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @PutMapping(ApiPath.BY_ID)
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable(name = "id") String id, @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(id, request))
                .build();
    }

    @DeleteMapping(ApiPath.BY_ID)
    public ApiResponse<Boolean> deleteCategory(@PathVariable(name = "id") String id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Boolean>builder()
                .result(true)
                .build();
    }

}
