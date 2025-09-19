package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.DescriptionAttr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescriptionAttrRepository extends JpaRepository<DescriptionAttr,String> {

    List<DescriptionAttr> findBySpu_Id(String spuId);

    boolean existsByNameAndSpu_Id(String name, String spu_id);
}
