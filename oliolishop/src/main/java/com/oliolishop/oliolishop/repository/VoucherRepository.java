package com.oliolishop.oliolishop.repository;

import com.oliolishop.oliolishop.entity.Voucher;
import com.oliolishop.oliolishop.enums.VoucherStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,String> {

    Optional<Voucher> findByVoucherCode(String voucherCode);

    @Query("""
            SELECT v from Voucher v
            WHERE (v.startDate < NOW() and v.endDate > NOW()) AND
            v.amount > 0 AND
            :totalPrice >= v.minOrderValue
            """)
    Optional<List<Voucher>> findByTotalPrice(@Param("totalPrice") BigDecimal totalPrice);

    Page<Voucher> findByStatus(VoucherStatus status, Pageable pageable);

    Page<Voucher> findByNameContainingIgnoreCaseAndStatus(String name, VoucherStatus status, Pageable pageable);

    Page<Voucher> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
