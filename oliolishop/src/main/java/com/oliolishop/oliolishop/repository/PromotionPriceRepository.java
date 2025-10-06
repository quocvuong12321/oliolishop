package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.PromotionPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionPriceRepository extends JpaRepository<PromotionPrice,String> {
}
