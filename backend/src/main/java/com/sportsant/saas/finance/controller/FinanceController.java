package com.sportsant.saas.finance.controller;

import com.sportsant.saas.finance.entity.Transaction;
import com.sportsant.saas.finance.service.FinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "财务中心", description = "交易记录、流水账单、税务计算")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    @Operation(summary = "Get transactions", description = "查询交易流水 (按日期范围)")
    public List<Transaction> getTransactions(
        @RequestParam String start,
        @RequestParam String end
    ) {
        return financeService.getTransactions(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/statement")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    @Operation(summary = "Get statement", description = "获取财务报表 (收入/支出/利润)")
    public Map<String, Object> getFinancialStatement(
        @RequestParam String start,
        @RequestParam String end
    ) {
        return financeService.getFinancialStatement(LocalDate.parse(start), LocalDate.parse(end));
    }

    @PostMapping("/ledger/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    @Operation(summary = "Generate ledger", description = "生成日记账 (日结)")
    public void generateDailyLedger() {
        financeService.generateDailyLedger();
    }

    @GetMapping("/predict/cashflow")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    @Operation(summary = "Predict Cash Flow", description = "AI 预测未来现金流")
    public Map<String, Object> predictCashFlow() {
        return financeService.predictCashFlow();
    }
}
