package com.sportsant.saas.risk.service;

import com.sportsant.saas.notification.service.NotificationCenterService;
import com.sportsant.saas.risk.entity.RiskAlert;
import com.sportsant.saas.risk.repository.RiskAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RiskService {

    @Autowired
    private RiskAlertRepository riskAlertRepository;

    @Autowired
    private NotificationCenterService notificationService;

    public List<RiskAlert> getAllAlerts() {
        return riskAlertRepository.findAll();
    }

    public List<RiskAlert> getActiveAlerts() {
        return riskAlertRepository.findByStatus("NEW");
    }

    // Called by other services (e.g., Finance, Auth)
    public void analyzeTransaction(String transactionId, BigDecimal amount, String userId) {
        // Simple Rule: High Value Transaction > 10000
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            RiskAlert alert = new RiskAlert();
            alert.setType("HIGH_VALUE");
            alert.setLevel("MEDIUM");
            alert.setSourceModule("FINANCE");
            alert.setReferenceId(transactionId);
            alert.setAmount(amount);
            alert.setDescription("High value transaction detected for User " + userId);
            riskAlertRepository.save(alert);
        }
    }

    // Called by AuthService
    public void analyzeLogin(String userId, String ip, String device) {
        // Mock Rule: Suspicious IP
        if ("1.2.3.4".equals(ip)) {
            RiskAlert alert = new RiskAlert();
            alert.setType("LOGIN");
            alert.setLevel("HIGH");
            alert.setSourceModule("AUTH");
            alert.setReferenceId(userId);
            alert.setDescription("Login from blacklisted IP: " + ip);
            riskAlertRepository.save(alert);

            // Send Notification to Admin
            try {
                notificationService.sendNotification(
                    "RISK_ALERT", 
                    "admin@sportsant.com", 
                    Map.of("type", "LOGIN", "userId", userId)
                );
            } catch (Exception e) {
                // Ignore notification failure
                System.err.println("Failed to send risk notification: " + e.getMessage());
            }
        }
    }

    @Transactional
    public RiskAlert resolveAlert(Long id, String resolution, String resolver) {
        RiskAlert alert = riskAlertRepository.findById(id).orElseThrow();
        alert.setStatus("RESOLVED");
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(resolver);
        alert.setDescription(alert.getDescription() + " [Resolution: " + resolution + "]");
        return riskAlertRepository.save(alert);
    }
}
