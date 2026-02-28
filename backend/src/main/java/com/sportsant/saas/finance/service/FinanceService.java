package com.sportsant.saas.finance.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.finance.engine.AccountingEngine;
import com.sportsant.saas.finance.entity.TransactionRecord;
import com.sportsant.saas.finance.entity.Voucher;
import com.sportsant.saas.finance.entity.Wallet;
import com.sportsant.saas.finance.repository.TransactionRepository;
import com.sportsant.saas.finance.repository.VoucherRepository;
import com.sportsant.saas.finance.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceService implements AiAware {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AccountingEngine accountingEngine;

    @Transactional
    public void processPayment(Long payerUserId, BigDecimal amount, String description) {
        // 1. Deduct from Member Wallet
        Wallet payerWallet = walletRepository.findByUserId(payerUserId)
                .orElseThrow(() -> new RuntimeException("Payer wallet not found"));
        
        if (payerWallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        payerWallet.setBalance(payerWallet.getBalance().subtract(amount));
        walletRepository.save(payerWallet);
        
        TransactionRecord debit = new TransactionRecord();
        debit.setWalletId(payerWallet.getId());
        debit.setType("PAYMENT_SENT");
        debit.setAmount(amount.negate());
        debit.setBalanceAfter(payerWallet.getBalance());
        debit.setDescription(description);
        transactionRepository.save(debit);

        // 2. Credit to Store Wallet (Assuming Store Admin User ID = 1 for MVP)
        Wallet storeWallet = walletRepository.findByUserId(1L)
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUserId(1L);
                    w.setBalance(BigDecimal.ZERO);
                    w.setCurrency("CNY");
                    return walletRepository.save(w);
                });
        
        storeWallet.setBalance(storeWallet.getBalance().add(amount));
        walletRepository.save(storeWallet);

        TransactionRecord credit = new TransactionRecord();
        credit.setWalletId(storeWallet.getId());
        credit.setType("PAYMENT_RECEIVED");
        credit.setAmount(amount);
        credit.setBalanceAfter(storeWallet.getBalance());
        credit.setDescription("Payment from User " + payerUserId + ": " + description);
        transactionRepository.save(credit);
    }

    public void recordPayment(BigDecimal amount, String memberCode, String desc) {
        // Legacy mock method, kept for backward compatibility if needed, 
        // but now we should use processPayment
    }
    
    public BigDecimal getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        BigDecimal revenue = transactionRepository.sumRevenueSince(startOfDay);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public Long getTodayVisitors() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        Long visitors = transactionRepository.countVisitorsSince(startOfDay);
        return visitors != null ? visitors : 0L;
    }

    public Voucher createVoucherFromEvent(String sourceType, Long sourceId, BigDecimal amount) {
        Voucher voucher = accountingEngine.generateVoucher(sourceType, sourceId, amount);
        return voucherRepository.save(voucher);
    }

    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    public Map<String, Object> calculateTax(String country, BigDecimal amount) {
        // Mock Tax Calculation
        Map<String, Object> result = new HashMap<>();
        if ("CN".equals(country)) {
            result.put("taxRate", 0.13);
            result.put("taxAmount", amount.multiply(new BigDecimal("0.13")));
        } else {
            result.put("taxRate", 0.0);
            result.put("taxAmount", BigDecimal.ZERO);
        }
        return result;
    }

    public Map<String, Object> predictCashFlow() {
        // Mock AI Prediction
        return Map.of(
            "forecast", List.of(
                Map.of("date", "2025-03-03", "balance", 100000),
                Map.of("date", "2025-03-04", "balance", 95000),
                Map.of("date", "2025-03-05", "balance", 105000)
            ),
            "confidence", 0.95
        );
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // AI: "High Tax Risk detected"
    }
}
