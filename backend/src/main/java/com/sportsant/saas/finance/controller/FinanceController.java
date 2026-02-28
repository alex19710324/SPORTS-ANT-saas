package com.sportsant.saas.finance.controller;

import com.sportsant.saas.finance.entity.Voucher;
import com.sportsant.saas.finance.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    // --- P0: Voucher Management ---
    @GetMapping("/vouchers")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public List<Voucher> listVouchers() {
        return financeService.getAllVouchers();
    }

    // --- P1: Tax & Prediction ---
    @PostMapping("/tax/calculate")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public Map<String, Object> calculateTax(@RequestBody Map<String, Object> payload) {
        String country = (String) payload.get("country");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        return financeService.calculateTax(country, amount);
    }

    @GetMapping("/cashflow/predict")
    @PreAuthorize("hasRole('FINANCE') or hasRole('ADMIN')")
    public Map<String, Object> predictCashFlow() {
        return financeService.predictCashFlow();
    }
}
