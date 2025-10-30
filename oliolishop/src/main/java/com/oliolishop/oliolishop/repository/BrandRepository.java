package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand,String> {

    Page<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
