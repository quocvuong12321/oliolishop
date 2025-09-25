package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.ProductSkuAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSkuAttrRepository extends JpaRepository<ProductSkuAttr,String> {

    List<ProductSkuAttr> findBySpu_Id(String id);





}
