package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,String> {

    Optional<List<Address>> findByCustomerId(String customerId);

}
