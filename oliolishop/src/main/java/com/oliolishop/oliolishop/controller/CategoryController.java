package com.oliolishop.oliolishop.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.oliolishop.oliolishop.configuration.CheckPermission;
import com.oliolishop.oliolishop.constant.ApiPath;
import com.oliolishop.oliolishop.dto.api.ApiResponse;
import com.oliolishop.oliolishop.dto.category.CategoryRequest;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(ApiPath.BASE + ApiPath.CATEGORY)
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @GetMapping
    public ApiResponse<Set<CategoryResponse>> getCategories() throws JsonProcessingException {
        return ApiResponse.<Set<CategoryResponse>>builder()
                .result(categoryService.loadParent())
                .build();
    }

    @GetMapping(ApiPath.BY_ID)
    public ApiResponse<Set<CategoryResponse>> getCategoryById(@PathVariable(name = "id") String id) {
        return ApiResponse.<Set<CategoryResponse>>builder()
                .result(categoryService.findChildren(id))
                .build();
    }

    @CheckPermission("CATEGORY_CREATE")
    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @CheckPermission("CATEGORY_UPDATE")
    @PutMapping(ApiPath.BY_ID)
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable(name = "id") String id, @RequestBody CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(id, request))
                .build();
    }

    @CheckPermission("CATEGORY_DELETE")
    @DeleteMapping(ApiPath.BY_ID)
    public ApiResponse<Boolean> deleteCategory(@PathVariable(name = "id") String id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Boolean>builder()
                .result(true)
                .build();
    }

}
