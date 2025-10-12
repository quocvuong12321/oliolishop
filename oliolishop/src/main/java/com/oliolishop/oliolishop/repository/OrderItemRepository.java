package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.Order;
import com.oliolishop.oliolishop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {

    Optional<OrderItem> findByOrderId(String orderId);

}
