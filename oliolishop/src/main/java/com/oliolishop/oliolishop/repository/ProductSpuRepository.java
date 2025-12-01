package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.dto.productspu.ProductSpuProjection;
import com.oliolishop.oliolishop.entity.ProductSku;
import com.oliolishop.oliolishop.entity.ProductSpu;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSpuRepository extends JpaRepository<ProductSpu, String> {



    @Query(
            value = """
                    SELECT
                        spu.product_spu_id AS productSpuId,
                        spu.name AS name,
                        spu.category_id AS categoryId,
                        spu.brand_id AS brandId,
                        MIN(sku.original_price) AS minPrice,
                        MAX(sku.original_price) AS maxPrice,
                        spu.image AS image,
                        spu.delete_status as deleteStatus
                    FROM product_spu spu
                    JOIN product_sku sku ON spu.product_spu_id = sku.product_spu_id
                    WHERE (:categoryId IS NULL OR spu.category_id = :categoryId)
                      AND (:brandId IS NULL OR spu.brand_id = :brandId)
                      AND (:search IS NULL OR spu.name LIKE CONCAT('%', :search, '%'))
                      AND spu.delete_status = 'Active'
                    GROUP BY spu.product_spu_id, spu.name, spu.category_id, spu.brand_id, spu.image
                    HAVING (:minPrice IS NULL OR MIN(sku.original_price) >= :minPrice)
                       AND (:maxPrice IS NULL OR MIN(sku.original_price) <= :maxPrice)
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT spu.product_spu_id)
                    FROM product_spu spu
                    JOIN product_sku sku ON spu.product_spu_id = sku.product_spu_id
                    WHERE (:categoryId IS NULL OR spu.category_id = :categoryId)
                      AND (:brandId IS NULL OR spu.brand_id = :brandId)
                      AND (:search IS NULL OR spu.name LIKE CONCAT('%', :search, '%'))
                      AND (:deleteStatus IS NULL OR spu.delete_status = :deleteStatus)
                    HAVING (:minPrice IS NULL OR MIN(sku.original_price) >= :minPrice)
                       AND (:maxPrice IS NULL OR MIN(sku.original_price) <= :maxPrice)
                    """,
            nativeQuery = true
    )
    Page<ProductSpuProjection> findProducts(
            @Param("categoryId") String categoryId,
            @Param("brandId") String brandId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("search") String search,
            @Param("deleteStatus") String deleteStatus,
            Pageable pageable
    );

    @Query( value = """ 
                    SELECT
                    spu.product_spu_id AS productSpuId,
                    spu.name AS name,
                    spu.category_id AS categoryId,
                    spu.brand_id AS brandId,
                    MIN(sku.original_price) AS minPrice,
                    MAX(sku.original_price) AS maxPrice,
                    spu.image AS image,
                    spu.delete_status as deleteStatus
                FROM product_spu spu
                JOIN product_sku sku ON spu.product_spu_id = sku.product_spu_id
                WHERE spu.product_spu_id = :id
            """,
            nativeQuery = true
            )
    Optional<ProductSpuProjection> findSpuById(@Param("id") String id);

    @Query("""
              SELECT p.id AS productSpuId,
                       p.name AS name,
                       p.image AS image,
                       p.brand.id AS brandId,
                       p.category.id AS categoryId,
                       MIN(s.originalPrice) AS minPrice,
                       MAX(s.originalPrice) AS maxPrice
                FROM ProductSpu p
                JOIN p.productSkus s
                WHERE p.id IN :ids and p.deleteStatus = Active
                GROUP BY p.id, p.name, p.image, p.brand.id, p.category.id
            """)
    List<ProductSpuProjection> findByIdIn(@Param("ids") List<String> ids);


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
    Optional<ProductSpu> findDetailById(@Param("id") String id, @Param("status") ProductSku.Status status);



    @Query("""
                SELECT spu
                FROM ProductSpu spu
                JOIN spu.productSkus sku
                WHERE sku.id = :skuId
            """)
    ProductSpu findBySkuId(@Param("skuId") String skuId);
    

    @Query(value = """
            SELECT
            spu.product_spu_id as productSpuId,
            spu.name as name,
            spu.category_id as categoryId,
            spu.brand_id as brandId,
            MIN(sku.original_price) as minPrice,
            MAX(sku.original_price) as maxPrice,
            spu.image as image,
            spu.delete_status as deleteStatus,
            SUM(oi.quantity) AS totalQuantitySold
            FROM `order` o
            JOIN order_item oi ON o.order_id = oi.order_id
            JOIN product_sku sku ON sku.product_sku_id = oi.product_sku_id
            JOIN product_spu spu ON spu.product_spu_id = sku.product_spu_id
            WHERE o.order_status = "delivered"
            GROUP BY spu.product_spu_id
            ORDER BY totalQuantitySold DESC
            LIMIT 20
            """,nativeQuery = true)
    List<ProductSpuProjection> getBestSellingProduct();


    @Query(value = """
            SELECT
                spu.product_spu_id as productSpuId,
                spu.name as name,
                spu.category_id as categoryId,
                spu.brand_id as brandId,
                MIN(sku.original_price) as minPrice,
                MAX(sku.original_price) as maxPrice,
                spu.image as image,
                spu.delete_status as deleteStatus
            FROM product_spu spu
            JOIN product_sku sku ON spu.product_spu_id = sku.product_spu_id
            WHERE spu.delete_status = "Active"
            GROUP BY spu.product_spu_id
            ORDER BY spu.create_date DESC
            LIMIT 20
            """,nativeQuery = true)
    List<ProductSpuProjection> getNewProduct();

}

