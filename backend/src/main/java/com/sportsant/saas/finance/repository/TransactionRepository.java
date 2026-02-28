package com.sportsant.saas.finance.repository;

import com.sportsant.saas.finance.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    List<TransactionRecord> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}
