package com.oliolishop.oliolishop.repository;


import com.oliolishop.oliolishop.entity.Transaction;
import com.oliolishop.oliolishop.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {

    List<Transaction> findByStatus(TransactionStatus status);

}
