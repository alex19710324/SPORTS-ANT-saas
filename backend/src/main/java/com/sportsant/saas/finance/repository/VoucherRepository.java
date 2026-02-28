package com.sportsant.saas.finance.repository;

import com.sportsant.saas.finance.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findBySourceType(String sourceType);
}
