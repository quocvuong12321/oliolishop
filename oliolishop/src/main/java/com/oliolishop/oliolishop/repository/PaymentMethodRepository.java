package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,String> {


}
