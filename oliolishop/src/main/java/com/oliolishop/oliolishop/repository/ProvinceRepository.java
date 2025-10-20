package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<Province,String> {
}
