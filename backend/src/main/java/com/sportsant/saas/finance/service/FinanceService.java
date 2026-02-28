package com.sportsant.saas.finance.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.finance.engine.AccountingEngine;
import com.sportsant.saas.finance.entity.Voucher;
import com.sportsant.saas.finance.entity.TransactionRecord;
import com.sportsant.saas.finance.repository.TransactionRepository;
import com.sportsant.saas.finance.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AccountingEngine accountingEngine;

    public void recordPayment(BigDecimal amount, String memberCode, String desc) {
        TransactionRecord record = new TransactionRecord();
        record.setWalletId(1L); // Store Wallet ID
        record.setType("PAYMENT");
        record.setAmount(amount);
        record.setBalanceAfter(BigDecimal.ZERO); // In real system, fetch wallet balance + amount
        record.setReferenceId(memberCode);
        record.setDescription(desc);
        transactionRepository.save(record);
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
