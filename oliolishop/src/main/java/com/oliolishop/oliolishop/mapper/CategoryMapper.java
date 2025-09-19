package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.BreadCrumbResponse;
import com.oliolishop.oliolishop.dto.category.CategoryRequest;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(ignore = true,target = "parent")
    Category toCategory(CategoryRequest request);

    @Mapping(target = "categoryId", source = "id")
    BreadCrumbResponse toBreadCrumbResponse(CategoryResponse response);

}
