package com.sportsant.saas.payment.service;

public class PaymentResult {
    private boolean success;
    private String transactionId;
    private String payUrl; // For redirect
    private String message;
    private String provider;

    public PaymentResult(boolean success, String transactionId, String payUrl, String message, String provider) {
        this.success = success;
        this.transactionId = transactionId;
        this.payUrl = payUrl;
        this.message = message;
        this.provider = provider;
    }

    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getPayUrl() { return payUrl; }
    public String getMessage() { return message; }
    public String getProvider() { return provider; }
}
