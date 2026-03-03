package com.sportsant.saas.finance.service;

import com.sportsant.saas.finance.entity.Transaction;
import com.sportsant.saas.finance.entity.Voucher;
import com.sportsant.saas.finance.repository.TransactionRepository;
import com.sportsant.saas.finance.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class FinanceService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private VoucherRepository voucherRepository;
    
    public BigDecimal getTodayRevenue() {
        return BigDecimal.valueOf(5000 + new Random().nextInt(10000));
    }
    
    public Integer getTodayVisitors() {
        return 50 + new Random().nextInt(100);
    }
    
    @Transactional
    public void createVoucher(String sourceType, Long sourceId, BigDecimal amount, String description) {
        createVoucher(sourceType, sourceId, amount, description, null);
    }

    @Transactional
    public void createVoucher(String sourceType, Long sourceId, BigDecimal amount, String description, String region) {
        Voucher voucher = new Voucher();
        voucher.setVoucherNo("V-" + System.currentTimeMillis());
        voucher.setSourceType(sourceType);
        voucher.setSourceId(sourceId);
        voucher.setAmount(amount);
        voucher.setCurrency("CNY"); // Default base currency for internal accounting
        voucher.setDescription(description);
        
        // Auto-fill region from context if not provided
        if (region == null) {
            region = "CN"; // Default
        }
        
        voucher.setRegion(region);
        
        voucherRepository.save(voucher);
    }
    
    public Transaction processPayment(Long memberId, Double amount, String description) {
        Transaction tx = new Transaction();
        tx.setDescription(description);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setAmount(amount);
        return transactionRepository.save(tx);
    }

    public Transaction processPayment(Long memberId, BigDecimal amount, String currency, String description) {
        Transaction tx = new Transaction();
        tx.setDescription(description);
        tx.setTransactionDate(LocalDateTime.now());
        tx.setAmount(amount.doubleValue());
        tx.setCurrency(currency);
        return transactionRepository.save(tx);
    }
    
    public Map<String, Object> getFinancialStatement(Object startDate, Object endDate) {
        return Map.of("revenue", 10000, "expenses", 5000, "profit", 5000);
    }
    
    public List<Transaction> getTransactions(Object startDate, Object endDate) {
        return List.of();
    }
    
    public Map<String, Object> generateDailyLedger() {
        return Map.of("status", "success");
    }
    
    public Map<String, Object> predictCashFlow() {
        return Map.of("prediction", "stable");
    }

    public BigDecimal calculateTax(String region, BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(0.1)); // 10% tax mock
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public void processMerchantTransaction(Long merchantId, String orderNo, Double amount, String currency, String type) {
        // Simple implementation for Open Platform
        Transaction tx = new Transaction();
        tx.setDescription("Merchant Transaction: " + type + " - Order: " + orderNo);
        tx.setType(type);
        tx.setCategory("MERCHANT");
        tx.setAmount(amount);
        tx.setCurrency(currency);
        tx.setSource("OPEN_PLATFORM");
        tx.setReferenceId("MERCHANT-" + merchantId);
        tx.setTransactionDate(LocalDateTime.now());
        
        transactionRepository.save(tx);
    }
}
