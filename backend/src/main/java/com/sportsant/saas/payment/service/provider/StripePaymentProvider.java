package com.sportsant.saas.payment.service.provider;

import com.sportsant.saas.payment.service.PaymentGateway;
import com.sportsant.saas.payment.service.PaymentResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service("stripePaymentProvider")
public class StripePaymentProvider implements PaymentGateway {

    @Override
    public PaymentResult initiatePayment(String orderNo, BigDecimal amount, String currency, String description) {
        // Mock Stripe API call - Redirect to internal mock page
        String txnId = "ch_" + UUID.randomUUID().toString().substring(0, 18);
        // Use relative URL or full URL if domain is configured
        String checkoutUrl = "http://localhost:8080/api/payment/mock-page?orderNo=" + orderNo + "&amount=" + amount + "&currency=" + currency;
        
        return new PaymentResult(true, txnId, checkoutUrl, "Stripe Checkout Session Created", "stripe");
    }

    @Override
    public String getProviderName() {
        return "stripe";
    }
}
