package com.sportsant.saas.finance.service;

import com.sportsant.saas.data.service.AnalyticsService;
import com.sportsant.saas.finance.entity.Transaction;
import com.sportsant.saas.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class FinanceService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AnalyticsService analyticsService;

    public List<Transaction> getTransactions(LocalDate start, LocalDate end) {
        return transactionRepository.findByTransactionDateBetween(start.atStartOfDay(), end.atTime(23, 59, 59));
    }

    @Transactional
    public Transaction recordTransaction(Transaction transaction) {
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }
        return transactionRepository.save(transaction);
    }

    @Transactional
    public void processPayment(Long userId, java.math.BigDecimal amount, String description) {
        Transaction tx = new Transaction();
        tx.setDescription(description);
        tx.setType("INCOME");
        tx.setCategory("SALES");
        tx.setAmount(amount.doubleValue());
        tx.setSource("POS");
        tx.setReferenceId("USER-" + userId);
        recordTransaction(tx);
    }

    public Double getTodayRevenue() {
        LocalDate today = LocalDate.now();
        List<Transaction> txs = getTransactions(today, today);
        return txs.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Integer getTodayVisitors() {
        // Mock visitor count for MVP
        return 125;
    }

    public Map<String, Object> getFinancialStatement(LocalDate start, LocalDate end) {
        List<Transaction> txs = getTransactions(start, end);
        
        double income = txs.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
                
        double expense = txs.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        // Mock data if empty for demo
        if (txs.isEmpty()) {
            income = 150000.0;
            expense = 85000.0;
        }

        Map<String, Object> report = new HashMap<>();
        report.put("totalIncome", income);
        report.put("totalExpense", expense);
        report.put("netProfit", income - expense);
        report.put("margin", (income > 0) ? ((income - expense) / income) * 100 : 0);
        
        return report;
    }

    // This would be a @Scheduled task in real life
    @Transactional
    public void generateDailyLedger() {
        // Mocking "Closing the Books"
        // 1. Get daily revenue from Analytics (which mocks Order/Booking sum)
        Map<String, Object> data = analyticsService.getDashboardData();
        Double totalRevenue = (Double) data.get("totalRevenue"); // Total accumulated
        
        // Create a transaction for "Daily Sales" (randomized for demo variation)
        Transaction income = new Transaction();
        income.setDescription("Daily Sales Consolidated");
        income.setType("INCOME");
        income.setCategory("SALES");
        income.setAmount(5000.0 + new Random().nextInt(2000));
        income.setSource("SYSTEM");
        income.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(income);
    }
}
