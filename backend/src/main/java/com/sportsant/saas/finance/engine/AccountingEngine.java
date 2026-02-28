package com.sportsant.saas.finance.engine;

import com.sportsant.saas.finance.entity.Voucher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock Accounting Engine.
 * Converts business events into accounting vouchers.
 */
@Component
public class AccountingEngine {

    public Map<String, Object> calculateTax(String country, BigDecimal amount) {
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

    public Voucher generateVoucher(String sourceType, Long sourceId, BigDecimal amount) {
        Voucher voucher = new Voucher();
        voucher.setVoucherNo("V-" + System.currentTimeMillis());
        voucher.setSourceType(sourceType);
        voucher.setSourceId(sourceId);
        voucher.setAmount(amount);
        voucher.setCurrency("CNY");

        // Simple Rule:
        if ("ORDER".equals(sourceType)) {
            voucher.setDebitAccount("1001 - Cash");
            voucher.setCreditAccount("6001 - Revenue");
        } else if ("PURCHASE".equals(sourceType)) {
            voucher.setDebitAccount("1403 - Raw Materials");
            voucher.setCreditAccount("1002 - Bank");
        }

        return voucher;
    }
}
