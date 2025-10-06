package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    boolean existsByCustomerId(String customerId);

    Optional<Cart> findByCustomerId(String customerId);

}
