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

    @GetMapping("/vouchers")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER')")
    public List<Voucher> getAllVouchers() {
        return financeService.getAllVouchers();
    }

    @PostMapping("/tax/calculate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER')")
    public Map<String, Object> calculateTax(@RequestBody Map<String, Object> payload) {
        String country = (String) payload.get("country");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        return financeService.calculateTax(country, amount);
    }

    @GetMapping("/forecast")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER')")
    public Map<String, Object> getCashFlowForecast() {
        return financeService.predictCashFlow();
    }
}
