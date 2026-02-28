package com.sportsant.saas.finance.service;

import com.sportsant.saas.finance.entity.Voucher;
import com.sportsant.saas.finance.repository.TransactionRepository;
import com.sportsant.saas.finance.repository.VoucherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class FinanceServiceTest {

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FinanceService financeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculateTax_CN() {
        BigDecimal amount = new BigDecimal("1000");
        Map<String, Object> result = financeService.calculateTax("CN", amount);
        
        assertNotNull(result);
        assertEquals(0.13, (Double) result.get("taxRate"), 0.001);
        BigDecimal taxAmount = (BigDecimal) result.get("taxAmount");
        assertEquals(130.0, taxAmount.doubleValue(), 0.01);
    }

    @Test
    public void testPredictCashFlow() {
        Map<String, Object> prediction = financeService.predictCashFlow();
        
        assertNotNull(prediction);
        assertTrue(prediction.containsKey("forecast"));
        assertTrue(prediction.containsKey("confidence"));
        assertEquals(0.95, (Double) prediction.get("confidence"), 0.001);
    }

    @Test
    public void testGetAllVouchers() {
        when(voucherRepository.findAll()).thenReturn(List.of(new Voucher(), new Voucher()));
        
        List<Voucher> vouchers = financeService.getAllVouchers();
        assertEquals(2, vouchers.size());
    }
}
