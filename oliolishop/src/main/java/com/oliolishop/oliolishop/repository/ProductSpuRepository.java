package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.dto.productspu.ProductSpuProjection;
import com.oliolishop.oliolishop.dto.productspu.ProductSpuResponse;
import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.entity.ProductSpu;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface ProductSpuRepository extends JpaRepository<ProductSpu, String> {


    @Query(
            value = "CALL GetProductList(:categoryId, :brandId, :minPrice, :maxPrice, :page, :size)",
            nativeQuery = true
    )
    List<ProductSpuProjection> findProducts(
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice,
            @Param("page") int page,
            @Param("size") int size
    );

    @Query(
            value = "CALL GetTotalElements(:categoryId, :brandId, :minPrice, :maxPrice)",
            nativeQuery = true
    )
    Integer getTotalElements(
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("minPrice") double minPrice,
            @Param("maxPrice") double maxPrice
    );

    @Query(value =
            """
                    SELECT spu
                    FROM ProductSpu spu
                    JOIN FETCH spu.productSkus sku
                    WHERE sku.sort = 0 AND spu.category.id = :categoryId
                    """)
    Page<ProductSpu> findByCategory(@Param("categoryId") String categoryId, Pageable pageable);

    @Query(value = """
                SELECT spu.*
                FROM product_spu spu
                WHERE spu.brand_id = :brandId
                ORDER BY RAND() LIMIT 20
            """, nativeQuery = true)
    List<ProductSpu> findRandom20ByBrandId(@Param("brandId") String brandId);

    @Query(value = "SELECT * FROM product_spu spu WHERE spu.category_id = :categoryId ORDER BY RAND() LIMIT 20",
            nativeQuery = true)
    List<ProductSpu> findRandom20ByCategoryId(@Param("categoryId") String categoryId);


    @Query("""
        SELECT spu
        FROM ProductSpu spu
        LEFT JOIN FETCH spu.productSkus skus
        LEFT JOIN FETCH spu.attrs attrs
        LEFT JOIN FETCH spu.skuAttrs skuAttrs
        LEFT JOIN FETCH spu.category category
        LEFT JOIN FETCH spu.brand brand
        WHERE spu.id = :id and skus.status = :status
    """)
    Optional<ProductSpu> findDetailById(@Param("id") String id, @Param("status")ProductSku.Status status);


    @Query(value = """
        SELECT COALESCE(MIN(sk.original_price), 0)
        FROM product_sku sk
        WHERE sk.product_spu_id = :spu_id
        """, nativeQuery = true)
    Double findMinPriceBySpuId(@Param("spu_id") String spu_id);


}

