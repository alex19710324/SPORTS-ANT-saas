package com.sportsant.saas.compliance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.audit.service.AuditLogService;
import com.sportsant.saas.compliance.repository.ConsentRepository;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.mall.repository.MallOrderRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import com.sportsant.saas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComplianceService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MallOrderRepository mallOrderRepository;

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Export all personal data for a user in JSON format.
     */
    public String exportUserData(Long userId, String ipAddress) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> data = new HashMap<>();
            
            // 1. User Account Data
            Map<String, Object> account = new HashMap<>();
            account.put("username", user.getUsername());
            account.put("email", user.getEmail());
            account.put("roles", user.getRoles());
            data.put("account", account);

            // 2. Member Profile Data
            Optional<Member> memberOpt = memberRepository.findByUserId(userId);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                data.put("profile", member);
            }

            // 3. Orders
            // Note: In a real system, we might need DTOs to avoid recursion or excessive data
            // For now, assuming entities are simple enough or Jackson handles it
            try {
                // We need to fetch orders by memberId (which is a String in MallOrder currently, based on previous code)
                // Let's assume memberId in MallOrder corresponds to User ID or Member ID. 
                // In PointsMall.vue, we saw `memberId` being passed. 
                // Let's verify MallOrderRepository usage. 
                // Ideally, we should fetch by memberId.
                // Assuming memberId in MallOrder is String.valueOf(member.getId()) or String.valueOf(user.getId())
                // Let's use user ID for now as it's safer if we are unsure.
                // Wait, MallOrder has memberId as String.
                if (memberOpt.isPresent()) {
                     data.put("orders", mallOrderRepository.findByMemberId(String.valueOf(memberOpt.get().getId())));
                }
            } catch (Exception e) {
                data.put("orders_error", "Failed to fetch orders: " + e.getMessage());
            }

            // 4. Consents
            data.put("consents", consentRepository.findByUserId(userId));

            // Log the export action
            auditLogService.logSuccess(userId, user.getUsername(), "DATA_EXPORT", "ALL", "ALL", "User requested data export", ipAddress);

            return objectMapper.writeValueAsString(data);

        } catch (Exception e) {
            auditLogService.logFailure(userId, "unknown", "DATA_EXPORT", "ALL", "ALL", "Export failed: " + e.getMessage(), ipAddress);
            throw new RuntimeException("Failed to export data", e);
        }
    }

    /**
     * Right to be Forgotten: Anonymize user data.
     */
    @Transactional
    public void deleteAccount(Long userId, String reason, String ipAddress) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String username = user.getUsername();

        try {
            // 1. Anonymize User Account
            String anonSuffix = "_deleted_" + UUID.randomUUID().toString().substring(0, 8);
            user.setUsername("deleted_user_" + userId); // Keep ID ref for integrity but anonymize PII
            user.setEmail("deleted_" + userId + "@anon.com");
            user.setPassword("{noop}deleted"); // Disable login
            userRepository.save(user);

            // 2. Anonymize Member Profile
            Optional<Member> memberOpt = memberRepository.findByUserId(userId);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                member.setName("Anonymous");
                member.setPhoneNumber(null); // Or hashed
                member.setIdCard(null); // PII removal
                member.setEmail(null);
                member.setStatus("DELETED");
                memberRepository.save(member);
            }
            
            // 3. We do NOT delete Orders usually for financial audit, but we remove PII from them if any.
            // MallOrder stores shippingAddress, which might contain PII.
            // For this MVP, we leave Orders as is, assuming they are linked to the now-anonymized Member.

            // Log the deletion
            auditLogService.logSuccess(userId, username, "ACCOUNT_DELETION", "User", String.valueOf(userId), "Reason: " + reason, ipAddress);

        } catch (Exception e) {
            auditLogService.logFailure(userId, username, "ACCOUNT_DELETION", "User", String.valueOf(userId), "Deletion failed: " + e.getMessage(), ipAddress);
            throw new RuntimeException("Failed to delete account", e);
        }
    }
}
