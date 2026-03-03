package com.sportsant.saas.payment.service;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult initiatePayment(String orderNo, BigDecimal amount, String currency, String description);
    String getProviderName();
}
