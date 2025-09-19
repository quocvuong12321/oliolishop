package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Category;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {


    List<Category> findByParentIsNull();

    @Query("SELECT c FROM Category c WHERE c.parent.id = :id")
    List<Category> findChildren(@Param("id") String id);


//    Category createCategory(String parent, Category c);
}
