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
        BigDecimal result = financeService.calculateTax("CN", amount);
        
        assertNotNull(result);
        assertEquals(100.0, result.doubleValue(), 0.01);
    }

    @Test
    public void testPredictCashFlow() {
        Map<String, Object> prediction = financeService.predictCashFlow();
        
        assertNotNull(prediction);
        assertTrue(prediction.containsKey("prediction"));
    }

    @Test
    public void testGetAllVouchers() {
        when(voucherRepository.findAll()).thenReturn(List.of(new Voucher(), new Voucher()));
        
        List<Voucher> vouchers = financeService.getAllVouchers();
        assertEquals(2, vouchers.size());
    }
}
