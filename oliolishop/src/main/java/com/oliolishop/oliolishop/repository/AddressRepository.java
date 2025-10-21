package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,String> {

    Optional<List<Address>> findByCustomerId(String customerId);

    @Query("""
            SELECT a FROM Address a
            JOIN FETCH a.ward w
            JOIN FETCH w.district d
            JOIN FETCH d.province p
            WHERE a.customer.id = :customerId
            """)
    Optional<List<Address>> findByCustomerIdWithDetail(@Param("customerId") String customerId);

    boolean existsByCustomerId(String customerId);

    Optional<Address> findByIdAndCustomerId(String id,String customerId);

    Optional<Address> findByCustomerIdAndIsDefault(String customerId,boolean isDefault);

}
