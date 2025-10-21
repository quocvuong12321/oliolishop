package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Order;
import com.oliolishop.oliolishop.service.OrderService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {

    Optional<Order> findByIdAndCustomerId(String id,String customerId);
}
