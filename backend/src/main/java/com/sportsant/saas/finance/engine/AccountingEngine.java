package com.sportsant.saas.finance.engine;

import com.sportsant.saas.finance.entity.Voucher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mock Accounting Engine.
 * Converts business events into accounting vouchers.
 */
@Component
public class AccountingEngine {

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
