package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.RatingLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingLikeRepository extends JpaRepository<RatingLike,Long> {

    Boolean existsByCustomer_IdAndRating_Id(String customerId,String ratingId);

    RatingLike findByCustomer_IdAndRating_Id(String customerId,String ratingId);

    @Query("""
        SELECT r.rating.id
        FROM RatingLike r
        WHERE (r.customer.id = :customerId AND r.customer.id IS NOT NULL)
        AND r.rating.id IN :ratingIds
        """)
    List<String> findLikedRatingIdsByCustomerIdAndRatingIds(
            @Param("customerId") String customerId,
            @Param("ratingIds") List<String> ratingIds);


}
