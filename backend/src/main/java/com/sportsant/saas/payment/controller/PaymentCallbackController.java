package com.sportsant.saas.payment.controller;

import com.sportsant.saas.communication.service.NotificationService;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.mall.entity.MallOrder;
import com.sportsant.saas.mall.repository.MallOrderRepository;
import com.sportsant.saas.membership.service.MembershipService;
import com.sportsant.saas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment/callback")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentCallbackController {

    @Autowired
    private MallOrderRepository orderRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipService membershipService;

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateCallback(@RequestBody Map<String, Object> payload) {
        String orderNo = (String) payload.get("orderNo");
        boolean success = (Boolean) payload.getOrDefault("success", true);

        MallOrder order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNo));

        if (success) {
            order.setStatus("PAID");
            order.setCompletedAt(LocalDateTime.now());
            
            // Auto complete virtual items
            if ("VIRTUAL".equals(order.getProductType()) || "COUPON".equals(order.getProductType())) {
                order.setStatus("COMPLETED");
                order.setRedeemCode(UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            }
            
            orderRepository.save(order);

            // Send Notification
            try {
                Long userId = Long.parseLong(order.getMemberId());
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    notificationService.sendLocalizedToUser(
                        user,
                        "notification.order.paid.title",
                        "notification.order.paid.message",
                        new Object[]{order.getOrderNo(), order.getCashPaid()},
                        "ORDER",
                        "/mall/orders"
                    );
                }
            } catch (Exception e) {
                System.err.println("Failed to send payment notification: " + e.getMessage());
            }

            return ResponseEntity.ok("Payment Success Processed");
        } else {
            order.setStatus("CANCELLED"); // Or PAYMENT_FAILED
            orderRepository.save(order);
            return ResponseEntity.ok("Payment Failure Processed");
        }
    }
}
