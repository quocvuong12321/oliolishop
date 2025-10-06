package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.ProductSku;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku,String> {

    @Query("""
            Select sku
            from ProductSku sku
            where sku.id = :id
            """)
    Optional<ProductSku> findByProductSkuId(@Param("id") String id);

}
