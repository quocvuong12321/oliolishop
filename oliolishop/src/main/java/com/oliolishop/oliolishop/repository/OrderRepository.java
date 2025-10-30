package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Order;
import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {

    Optional<Order> findByIdAndCustomerId(String id,String customerId);

    Page<Order> findByCustomerIdAndOrderStatusIn(String customerId, List<OrderStatus> statuses, Pageable pageable);

    Page<Order> findByCustomerId(String customerId,Pageable pageable);

    Page<Order> findByOrderStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByOrderStatusIn(List<OrderStatus> statuses, Pageable pageable);

}

