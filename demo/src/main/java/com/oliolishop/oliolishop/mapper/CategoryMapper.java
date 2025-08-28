package com.oliolishop.oliolishop.mapper;


import com.oliolishop.oliolishop.dto.category.CategoryRequest;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface CategoryMapper {
    @Mapping(ignore = true,target = "parent")
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(ignore = true,target = "parent")
    Category toCategory(CategoryRequest request);
}
