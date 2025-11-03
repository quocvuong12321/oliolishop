package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Brand;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand,String> {

    Page<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
            select distinct b from Brand b
            join ProductSpu p on b.id = p.brand.id
            where p.category.id = :categoryId
            """)
    Page<Brand> findDistinctBrandsByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);

}
