package com.sportsant.saas.finance.repository;

import com.sportsant.saas.finance.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    List<TransactionRecord> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    @Query("SELECT SUM(t.amount) FROM TransactionRecord t WHERE t.type = 'PAYMENT' AND t.createdAt >= :startOfDay")
    BigDecimal sumRevenueSince(@Param("startOfDay") LocalDateTime startOfDay);
    
    @Query("SELECT COUNT(DISTINCT t.referenceId) FROM TransactionRecord t WHERE t.type = 'PAYMENT' AND t.createdAt >= :startOfDay")
    Long countVisitorsSince(@Param("startOfDay") LocalDateTime startOfDay);
}
