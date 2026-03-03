package com.sportsant.saas.finance.service;

import com.sportsant.saas.common.context.UserContext;
import com.sportsant.saas.data.service.AnalyticsService;
import com.sportsant.saas.finance.engine.TaxEngine;
import com.sportsant.saas.finance.entity.Transaction;
import com.sportsant.saas.finance.entity.Voucher;
import com.sportsant.saas.finance.repository.TransactionRepository;
import com.sportsant.saas.finance.repository.VoucherRepository;
import com.sportsant.saas.globalization.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private VoucherRepository voucherRepository;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private TaxEngine taxEngine;

    @Autowired
    private CurrencyService currencyService;

    public Map<String, Object> calculateTax(String region, BigDecimal amount) {
        // Use TaxEngine for robust calculation
        // Defaulting to "VAT:STANDARD" category for now
        TaxEngine.TaxResult result = taxEngine.calculate(region, "VAT:STANDARD", amount);
        
        Map<String, Object> map = new HashMap<>();
        map.put("taxRate", result.getRate());
        map.put("taxAmount", result.getAmount());
        map.put("rule", result.getAppliedRule());
        return map;
    }

    public Map<String, Object> predictCashFlow() {
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("forecast", 10000.0);
        prediction.put("confidence", 0.95);
        return prediction;
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public List<Transaction> getTransactions(LocalDate start, LocalDate end) {
        return transactionRepository.findByTransactionDateBetween(start.atStartOfDay(), end.atTime(23, 59, 59));
    }

    public Map<String, Object> getFinancialStatement(LocalDate start, LocalDate end) {
        // Mock statement
        Map<String, Object> statement = new HashMap<>();
        statement.put("revenue", 50000.0);
        statement.put("expenses", 20000.0);
        statement.put("profit", 30000.0);
        return statement;
    }
    
    public void generateDailyLedger() {
        // Mock generation
        System.out.println("Generating daily ledger...");
    }

    @Transactional
    public Transaction recordTransaction(Transaction transaction) {
        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }
        return transactionRepository.save(transaction);
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
            region = UserContext.getLocale(); 
            if (region == null) region = "CN"; // Default
            else if (region.contains("-")) region = region.split("-")[1];
        }
        
        voucher.setRegion(region);
        
        voucherRepository.save(voucher);
    }

    public BigDecimal getTodayRevenue() {
        // Proxy to AnalyticsService or calculate from transactions
        // For now, simple calculation
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        return transactionRepository.sumAmountByDateRange(start, end);
    }
    
    public Integer getTodayVisitors() {
        // Proxy to AnalyticsService
        return analyticsService.getTodayVisitorCount();
    }

    @Transactional
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


    @Transactional
    public void processPayment(Long userId, java.math.BigDecimal amount, String description) {
        processPayment(userId, amount, description, "CNY");
    }

    @Transactional
    public void processPayment(Long userId, Double amount, String description) {
        processPayment(userId, BigDecimal.valueOf(amount), description, "CNY");
    }

    @Transactional
    public void processPayment(Long userId, Double amount, String description, String currency) {
        processPayment(userId, BigDecimal.valueOf(amount), description, currency);
    }

    @Transactional
    public void processPayment(Long userId, java.math.BigDecimal amount, String description, String currency) {
        Transaction tx = new Transaction();
        tx.setDescription(description);
        tx.setType("INCOME");
        tx.setCategory("SALES");
        tx.setAmount(amount.doubleValue());
        tx.setSource("POS");
        tx.setReferenceId("USER-" + userId);
        tx.setCurrency(currency != null ? currency : "CNY");
        
        BigDecimal amountInCNY = amount;
        // Use CurrencyService for Exchange Rate (to Base Currency CNY)
        if (currency != null && !"CNY".equalsIgnoreCase(currency)) {
             amountInCNY = currencyService.convertByCurrency(amount, currency, "CNY");
             // Rate = CNY Amount / Original Amount
             if (amount.compareTo(BigDecimal.ZERO) != 0) {
                 tx.setExchangeRate(amountInCNY.divide(amount, 4, BigDecimal.ROUND_HALF_UP).doubleValue());
             } else {
                 tx.setExchangeRate(1.0);
             }
        } else {
             tx.setExchangeRate(1.0);
        }

        recordTransaction(tx);
        
        // Create corresponding Voucher in Base Currency (CNY)
        createVoucher("PAYMENT", tx.getId(), amountInCNY, description);
    }
}
