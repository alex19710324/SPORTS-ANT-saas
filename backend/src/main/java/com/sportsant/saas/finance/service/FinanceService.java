package com.sportsant.saas.finance.service;

import com.sportsant.saas.data.service.AnalyticsService;
import com.sportsant.saas.finance.entity.Transaction;
import com.sportsant.saas.finance.entity.Voucher;
import com.sportsant.saas.finance.repository.TransactionRepository;
import com.sportsant.saas.finance.repository.VoucherRepository;
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

    public Map<String, Object> calculateTax(String region, BigDecimal amount) {
        Map<String, Object> result = new HashMap<>();
        double rate = "CN".equals(region) ? 0.13 : 0.10;
        result.put("taxRate", rate);
        result.put("taxAmount", amount.multiply(BigDecimal.valueOf(rate)));
        return result;
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
        // 1. Get Vouchers (Accounting Data)
        List<Voucher> vouchers = voucherRepository.findByPostedAtBetween(start.atStartOfDay(), end.atTime(23, 59, 59));
        
        // 2. Aggregate by Account Type
        double revenue = vouchers.stream()
            .filter(v -> "6001-Revenue".equals(v.getCreditAccount()))
            .mapToDouble(v -> v.getAmount().doubleValue())
            .sum();
            
        double cogs = vouchers.stream() // Cost of Goods Sold (Raw Materials)
            .filter(v -> "1403-Raw Materials".equals(v.getDebitAccount()))
            .mapToDouble(v -> v.getAmount().doubleValue())
            .sum();

        double expense = vouchers.stream() // Salary, Rent, etc.
            .filter(v -> v.getDebitAccount() != null && v.getDebitAccount().startsWith("6602"))
            .mapToDouble(v -> v.getAmount().doubleValue())
            .sum();
            
        // 3. Tax Calculation (Mock based on Revenue)
        double tax = revenue * 0.13; // 13% VAT assumption for CN
        
        // 4. Fallback Mock Data if Empty (for Demo)
        if (vouchers.isEmpty()) {
            revenue = 150000.0;
            cogs = 45000.0;
            expense = 40000.0;
            tax = revenue * 0.13;
        }

        double netProfit = revenue - cogs - expense - tax;

        Map<String, Object> report = new HashMap<>();
        report.put("totalIncome", revenue); // Revenue
        report.put("cogs", cogs); // Cost of Goods
        report.put("operatingExpense", expense); // OpEx
        report.put("tax", tax);
        report.put("netProfit", netProfit);
        report.put("margin", (revenue > 0) ? (netProfit / revenue) * 100 : 0);
        
        return report;
    }

    // This would be a @Scheduled task in real life
    @Transactional
    public void generateDailyLedger() {
        // Mocking "Closing the Books"
        // 1. Get daily revenue from Analytics (which mocks Order/Booking sum)
        Map<String, Object> data = analyticsService.getDashboardData();
        System.out.println("Generating daily ledger with data: " + data);
        
        // Create a voucher for "Daily Sales" (randomized for demo variation)
        createVoucher("DAILY_CLOSING", 0L, BigDecimal.valueOf(5000.0 + new Random().nextInt(2000)), "Daily Sales Consolidated", "CN");
    }

    @Transactional
    public Voucher createVoucher(String sourceType, Long sourceId, BigDecimal amount, String description, String region) {
        Voucher voucher = new Voucher();
        voucher.setVoucherNo("V-" + System.currentTimeMillis());
        voucher.setSourceType(sourceType);
        voucher.setSourceId(sourceId);
        voucher.setAmount(amount);
        voucher.setDescription(description);
        voucher.setRegion(region != null ? region : "CN");
        voucher.setCurrency("CNY");
        
        // Simple Rule Engine for Account Mapping
        if ("POS_SALE".equals(sourceType) || "DAILY_CLOSING".equals(sourceType)) {
            voucher.setDebitAccount("1001-Cash/Bank");
            voucher.setCreditAccount("6001-Revenue");
        } else if ("PURCHASE".equals(sourceType)) {
            voucher.setDebitAccount("1403-Raw Materials");
            voucher.setCreditAccount("1002-Bank Deposit");
        } else if ("PAYROLL".equals(sourceType)) {
            voucher.setDebitAccount("6602-Admin Expense-Salary");
            voucher.setCreditAccount("2211-Payable Salary");
        } else {
            voucher.setDebitAccount("9999-Suspense");
            voucher.setCreditAccount("9999-Suspense");
        }

        // Auto-Calculate Tax (Tax Engine Placeholder)
        Map<String, Object> taxInfo = calculateTax(voucher.getRegion(), amount);
        // In a real system, we'd create a separate Tax Voucher or multi-line entry
        // For MVP, we just log it or store it (could add taxAmount field to Voucher later)
        System.out.println("Tax Calculated for " + sourceType + ": " + taxInfo);

        return voucherRepository.save(voucher);
    }
}
