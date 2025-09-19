package com.oliolishop.oliolishop.service;

import com.oliolishop.oliolishop.dto.category.CategoryRequest;
import com.oliolishop.oliolishop.dto.category.CategoryResponse;
import com.oliolishop.oliolishop.entity.Category;
import com.oliolishop.oliolishop.exception.AppException;
import com.oliolishop.oliolishop.exception.ErrorCode;
import com.oliolishop.oliolishop.mapper.CategoryMapper;
import com.oliolishop.oliolishop.repository.CategoryRepository;
import com.oliolishop.oliolishop.ultils.AppUtils;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public List<CategoryResponse> loadParent() {
        return categoryRepository.findByParentIsNull().stream().map(categoryMapper::toCategoryResponse).toList();
    }

    public List<CategoryResponse> findChildren(String id) {
        Category cate = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

        //        lstChildren.forEach(c -> c.setParent(id));
        return categoryRepository.findChildren(cate.getId()).stream().map(categoryMapper::toCategoryResponse).toList();
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category c = categoryMapper.toCategory(request);

        c.setIsLeaf(true);

        if (request.getParent() != null) {

            Category parent = categoryRepository.findById(request.getParent()).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

            c.setParent(parent);

            if (parent.getIsLeaf()) {
                parent.setIsLeaf(false);
                categoryRepository.save(parent);
            }
        } else {
            c.setParent(null);
        }

        c.setId(UUID.randomUUID().toString());

        String slug = AppUtils.toSlug(c.getName());
        c.setKey(slug);
        c.setUrl(AppUtils.convertToURL(slug, c.getId()));

        return categoryMapper.toCategoryResponse(categoryRepository.save(c));
    }

    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

        c.setName(request.getName());

        String slug = AppUtils.toSlug(request.getName());

        c.setKey(slug);

        c.setUrl(AppUtils.convertToURL(slug, id));

        return categoryMapper.toCategoryResponse(categoryRepository.save(c));
    }

    public void deleteCategory(String id) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));

        categoryRepository.deleteById(id);
    }

    public List<CategoryResponse> categoryProduct(Category category) {

        List<Category> lst = new ArrayList<>();
        collectParents(category,lst);
        return lst.reversed().stream().map(categoryMapper::toCategoryResponse).toList();
    }

    private void collectParents(Category category, List<Category> parents) {
        parents.add(category);
        if (category.getParent() != null) {
            collectParents(category.getParent(), parents);
        }
    }

}
