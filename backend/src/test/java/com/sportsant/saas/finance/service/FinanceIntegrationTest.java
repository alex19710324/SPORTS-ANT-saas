package com.sportsant.saas.finance.service;

import com.sportsant.saas.finance.engine.TaxEngine;
import com.sportsant.saas.finance.entity.Voucher;
import com.sportsant.saas.finance.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FinanceIntegrationTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private TaxEngine taxEngine;

    @InjectMocks
    private FinanceService financeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetFinancialStatement_WithVouchers() {
        // Mock Data: 
        // 1. Sale: 1000 (Rev)
        // 2. Cost: 400 (COGS)
        // 3. Salary: 200 (Expense)
        
        Voucher v1 = new Voucher();
        v1.setAmount(BigDecimal.valueOf(1000));
        v1.setCreditAccount("6001-Revenue");
        v1.setSourceType("POS_SALE");
        
        Voucher v2 = new Voucher();
        v2.setAmount(BigDecimal.valueOf(400));
        v2.setDebitAccount("1403-Raw Materials");
        v2.setSourceType("PURCHASE");

        Voucher v3 = new Voucher();
        v3.setAmount(BigDecimal.valueOf(200));
        v3.setDebitAccount("6602-Admin Expense-Salary");
        v3.setSourceType("PAYROLL");

        when(voucherRepository.findByPostedAtBetween(any(), any()))
            .thenReturn(Arrays.asList(v1, v2, v3));

        // Mock TaxEngine
        when(taxEngine.calculate(any(), any(), any()))
            .thenReturn(new TaxEngine.TaxResult(0.13, BigDecimal.valueOf(130.0), "CN:VAT:STANDARD"));

        Map<String, Object> report = financeService.getFinancialStatement(LocalDate.now(), LocalDate.now());

        assertEquals(1000.0, report.get("totalIncome"));
        assertEquals(400.0, report.get("cogs"));
        assertEquals(200.0, report.get("operatingExpense"));
        
        // Tax = 1000 * 0.13 = 130
        assertEquals(130.0, report.get("tax"));
        
        // Net = 1000 - 400 - 200 - 130 = 270
        assertEquals(270.0, report.get("netProfit"));
    }
}
