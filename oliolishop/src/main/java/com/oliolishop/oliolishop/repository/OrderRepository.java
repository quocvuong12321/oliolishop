package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {



}
