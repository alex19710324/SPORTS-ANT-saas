package com.sportsant.saas.finance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "finance_transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String type; // INCOME, EXPENSE

    @Column(nullable = false)
    private String category; // SALES, RENT, SALARY, MAINTENANCE, UTILITIES, MARKETING

    @Column(nullable = false)
    private Double amount;

    private LocalDateTime transactionDate;

    private String source; // ORDER, BOOKING, SYSTEM, MANUAL, OPEN_PLATFORM

    private String referenceId; // e.g. Order ID, Merchant AppKey

    private Long tenantId; // Associated Tenant ID for SaaS/Open Platform transactions

    private String currency; // CNY, USD, EUR
    private Double exchangeRate; // To Base Currency (CNY)

    @PrePersist
    protected void onCreate() {
        if (currency == null) currency = "CNY";
        if (exchangeRate == null) exchangeRate = 1.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Double getExchangeRate() { return exchangeRate; }
    public void setExchangeRate(Double exchangeRate) { this.exchangeRate = exchangeRate; }
}
