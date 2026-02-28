package com.sportsant.saas.finance.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_vouchers")
@Data
@NoArgsConstructor
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voucherNo; // e.g., V-20250302-001
    private String sourceType; // ORDER, PURCHASE, PAYROLL
    private Long sourceId;

    private BigDecimal amount;
    private String currency; // CNY, USD

    private String debitAccount; // 借方科目
    private String creditAccount; // 贷方科目

    private String status; // DRAFT, POSTED, REVERSED
    private LocalDateTime postedAt;

    @PrePersist
    protected void onCreate() {
        postedAt = LocalDateTime.now();
        status = "POSTED";
    }
}
