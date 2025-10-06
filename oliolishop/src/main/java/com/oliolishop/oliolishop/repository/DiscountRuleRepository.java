package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule,String> {
}
