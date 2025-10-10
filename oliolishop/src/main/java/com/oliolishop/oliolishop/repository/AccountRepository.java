package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AccountRepository extends JpaRepository<Account,String> {

    boolean existsByUsername(String username);
    Optional<Account> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Account> findByEmail(String email);

}
