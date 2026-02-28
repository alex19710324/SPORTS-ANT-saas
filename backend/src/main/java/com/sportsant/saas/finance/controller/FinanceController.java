package com.sportsant.saas.finance.controller;

import com.sportsant.saas.finance.entity.Transaction;
import com.sportsant.saas.finance.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @GetMapping("/transactions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public List<Transaction> getTransactions(
        @RequestParam String start,
        @RequestParam String end
    ) {
        return financeService.getTransactions(LocalDate.parse(start), LocalDate.parse(end));
    }

    @GetMapping("/statement")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public Map<String, Object> getFinancialStatement(
        @RequestParam String start,
        @RequestParam String end
    ) {
        return financeService.getFinancialStatement(LocalDate.parse(start), LocalDate.parse(end));
    }

    @PostMapping("/ledger/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FINANCE')")
    public void generateDailyLedger() {
        financeService.generateDailyLedger();
    }
}
