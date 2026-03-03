package com.sportsant.saas.finance.repository;

import com.sportsant.saas.finance.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end AND t.type = 'INCOME'")
    BigDecimal sumAmountByDateRange(LocalDateTime start, LocalDateTime end);
}
