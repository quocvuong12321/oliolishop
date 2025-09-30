package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,String> {

    Optional<Customer> findByAccountId(String accountId);

}
