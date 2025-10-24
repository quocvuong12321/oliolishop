package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.Transaction;
import com.oliolishop.oliolishop.enums.OrderStatus;
import com.oliolishop.oliolishop.enums.TransactionStatus;
import com.oliolishop.oliolishop.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {

    List<Transaction> findByStatusAndTransactionType(TransactionStatus status,TransactionType type);

    Optional<Transaction> findByOrderId(String orderId);

    Optional<Transaction> findByOrderIdAndTransactionTypeAndStatus(String orderId, TransactionType type, TransactionStatus status);
}
