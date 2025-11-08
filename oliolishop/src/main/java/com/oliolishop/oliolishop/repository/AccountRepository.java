package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Account;
import com.oliolishop.oliolishop.entity.Voucher;
import com.oliolishop.oliolishop.enums.VoucherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account,String>, JpaSpecificationExecutor<Account> {

    boolean existsByUsername(String username);
    Optional<Account> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Account> findByEmail(String email);

    Page<Account> findByStatus(Account.AccountStatus status, Pageable pageable);

    Page<Account> findByPhoneNumberContainingIgnoreCaseAndStatus(String phone, Account.AccountStatus status, Pageable pageable);

    Page<Account>  findByPhoneNumberContainingIgnoreCase(String phone, Pageable pageable);

}
