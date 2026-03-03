package com.sportsant.saas.payment.service.provider;

import com.sportsant.saas.payment.service.PaymentGateway;
import com.sportsant.saas.payment.service.PaymentResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service("wechatPaymentProvider")
public class WechatPaymentProvider implements PaymentGateway {

    @Override
    public PaymentResult initiatePayment(String orderNo, BigDecimal amount, String currency, String description) {
        // Mock WeChat Pay API call (Native/JSAPI)
        String prepayId = "wx" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String codeUrl = "http://localhost:8080/api/payment/mock-page?orderNo=" + orderNo + "&amount=" + amount + "&currency=" + currency;
        
        return new PaymentResult(true, prepayId, codeUrl, "WeChat Prepay Order Created", "wechat");
    }

    @Override
    public String getProviderName() {
        return "wechat";
    }
}
