package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,String> {

    Optional<Voucher> findByVoucherCode(String voucherCode);
}
