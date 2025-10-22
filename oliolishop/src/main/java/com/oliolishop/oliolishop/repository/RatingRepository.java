package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.Rating;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating,String> {

    Page<Rating> findByProductSpu_IdOrderByCreateDateDesc(String productSpuId,Pageable pageable);

    boolean existsByCustomer_IdAndOrderItem_Id(String customerId,String orderItemId);

    long countByProductSpu_Id(String spuId);

    @Query("SELECT AVG(r.star) FROM Rating r WHERE r.productSpu.id = :spuId AND r.isHidden = false")
    Double getAverageStarByProductSpuId(@Param("spuId") String spuId);

}
