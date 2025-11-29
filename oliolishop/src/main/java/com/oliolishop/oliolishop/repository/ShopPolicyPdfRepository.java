package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.ShopPolicyPdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopPolicyPdfRepository extends JpaRepository<ShopPolicyPdf, Long> {

    Optional<ShopPolicyPdf> findByItem(String item);

    boolean existsByItem(String item);
}

