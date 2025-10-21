package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {
    List<Ward> findByDistrictId(String districtId);

    @Query("""
            SELECT w FROM Ward w
            JOIN FETCH w.district d
            JOIN FETCH d.province
            WHERE w.id = :id
            """)
    Optional<Ward> findByIdWithDetails(@Param("id") String wardId);
}
