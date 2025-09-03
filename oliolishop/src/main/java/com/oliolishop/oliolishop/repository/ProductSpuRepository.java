package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.dto.productspu.ProductSpuResponse;
import com.oliolishop.oliolishop.entity.ProductSpu;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public interface ProductSpuRepository extends JpaRepository<ProductSpu,String> {

    @Query(value =
            """
                    SELECT spu
                    FROM ProductSpu spu
                    JOIN FETCH spu.productSkus sku
                    WHERE sku.sort = 0
                    """)
    Page<ProductSpu> findProducts(Pageable pageable);

    @Query(value =
            """
                    SELECT spu
                    FROM ProductSpu spu
                    JOIN FETCH spu.productSkus sku
                    WHERE sku.sort = 0 AND spu.categoryId = :categoryId
                    """)
    Page<ProductSpu> findByCategory(@Param("categoryId" ) String categoryId, Pageable pageable);
}

