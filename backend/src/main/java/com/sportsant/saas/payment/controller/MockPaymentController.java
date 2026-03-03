package com.sportsant.saas.payment.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@Controller
@Hidden
public class MockPaymentController {

    @GetMapping("/api/payment/mock-page")
    @ResponseBody
    public String mockPaymentPage(@RequestParam String orderNo, @RequestParam BigDecimal amount, @RequestParam String currency) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Mock Payment Gateway</title>
                <style>
                    body { font-family: sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background-color: #f0f2f5; }
                    .card { background: white; padding: 40px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); text-align: center; width: 400px; }
                    h2 { color: #333; }
                    .amount { font-size: 24px; color: #409eff; margin: 20px 0; font-weight: bold; }
                    button { padding: 12px 24px; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; width: 100%; margin-top: 10px; }
                    .btn-pay { background-color: #67c23a; color: white; }
                    .btn-cancel { background-color: #f56c6c; color: white; }
                </style>
            </head>
            <body>
                <div class="card">
                    <h2>Secure Payment</h2>
                    <p>Order: <strong>%s</strong></p>
                    <div class="amount">%s %s</div>
                    
                    <button class="btn-pay" onclick="pay(true)">Confirm Payment</button>
                    <button class="btn-cancel" onclick="pay(false)">Cancel</button>

                    <script>
                        function pay(success) {
                            fetch('/api/payment/callback/simulate', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({
                                    orderNo: '%s',
                                    success: success
                                })
                            })
                            .then(response => {
                                if (response.ok) {
                                    alert(success ? 'Payment Successful!' : 'Payment Cancelled');
                                    window.close(); // Close tab if opened in new tab
                                } else {
                                    alert('Error processing payment');
                                }
                            });
                        }
                    </script>
                </div>
            </body>
            </html>
            """.formatted(orderNo, currency, amount, orderNo);
    }
}
