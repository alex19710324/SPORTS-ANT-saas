package com.sportsant.saas.workbench.service;

import com.sportsant.saas.entity.ERole;
import com.sportsant.saas.entity.Role;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.service.MembershipService;
import com.sportsant.saas.repository.RoleRepository;
import com.sportsant.saas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class FrontDeskService {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private com.sportsant.saas.inventory.service.InventoryService inventoryService;

    public Map<String, Object> getFrontDeskOverview() {
        // Mock data
        Map<String, Object> data = new HashMap<>();
        data.put("todayTarget", 5000.00);
        data.put("currentSales", financeService.getTodayRevenue()); // Real data
        data.put("pendingCheckins", 3);
        data.put("pendingVerifications", 2);
        return data;
    }

    public Member quickCheckIn(String memberCode) {
        Member member = membershipService.findMemberByCodeOrPhone(memberCode);
        return membershipService.dailyCheckIn(member.getUserId());
    }

    @Transactional
    public Member registerMember(String name, String phoneNumber, String locale) {
        // 1. Check if user exists by phone (username)
        if (userRepository.existsByUsername(phoneNumber)) {
            throw new RuntimeException("User with this phone number already exists.");
        }
        // 2. Create User
        String email = phoneNumber + "@member.local";
        if (userRepository.existsByEmail(email)) {
            // Should be rare if username is unique, but check anyway
            email = phoneNumber + "_" + System.currentTimeMillis() + "@member.local";
        }

        User user = new User(phoneNumber, email, encoder.encode("123456"));
        
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        
        user = userRepository.save(user);

        // 3. Create Member (With Locale)
        // Note: idCard is null for quick front desk reg. 
        // If compliance requires ID Card (e.g. Italy), this might fail validation in MembershipService.
        // The Front Desk UI should prompt for ID Card if locale requires it.
        return membershipService.createMember(user.getId(), name, phoneNumber, null, locale);
    }

    @Transactional
    public Member registerMember(String name, String phoneNumber, String locale, String idCard) {
         // Overloaded method to support ID Card
        if (userRepository.existsByUsername(phoneNumber)) {
            throw new RuntimeException("User with this phone number already exists.");
        }
        String email = phoneNumber + "@member.local";
        if (userRepository.existsByEmail(email)) email = phoneNumber + "_" + System.currentTimeMillis() + "@member.local";

        User user = new User(phoneNumber, email, encoder.encode("123456"));
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow());
        user.setRoles(roles);
        user = userRepository.save(user);

        return membershipService.createMember(user.getId(), name, phoneNumber, idCard, locale);
    }

    @Transactional
    public Member processTopUp(String memberCode, Double amount, String paymentMethod) {
        Member member = membershipService.findMemberByCodeOrPhone(memberCode);
        
        // 1. Update Balance
        membershipService.topUp(member.getUserId(), amount);
        
        // 2. Process Payment
        financeService.processPayment(
            member.getUserId(), 
            amount, 
            "Top-Up (" + paymentMethod + ")"
        );
        
        // 3. Finance Record (Deferred Revenue)
        // Debit: Cash (1001), Credit: Advance from Customers (2203)
        financeService.createVoucher(
            "TOP_UP", 
            member.getId(), 
            BigDecimal.valueOf(amount), 
            "Member Top-Up: " + memberCode, 
            "CN"
        );
        
        return member;
    }

    @Transactional
    public Member processSale(String memberCode, Double amount) {
        Member member = membershipService.findMemberByCodeOrPhone(memberCode);
        
        // Use the new Finance Service method for real double-entry accounting
        financeService.processPayment(
            member.getUserId(), 
            amount, 
            "POS Sale to " + member.getName()
        );
        
        // Also create a formal Accounting Voucher (Business-Finance Integration)
        financeService.createVoucher("POS_SALE", member.getId(), BigDecimal.valueOf(amount), "POS Sale: " + member.getName(), "CN");
        
        // Still trigger membership updates (points/growth)
        return membershipService.simulatePurchase(member.getUserId(), amount.intValue());
    }

    @Transactional
    public Member processCartSale(String memberCode, java.util.List<Map<String, Object>> cartItems, String paymentMethod) {
        Member member = membershipService.findMemberByCodeOrPhone(memberCode);
        double totalAmount = 0;
        StringBuilder desc = new StringBuilder("POS Sale: ");

        for (Map<String, Object> item : cartItems) {
            String sku = (String) item.get("sku");
            int qty = (Integer) item.get("quantity");
            
            // Deduct Stock
            com.sportsant.saas.inventory.entity.InventoryItem invItem = inventoryService.adjustStock(sku, -qty, "POS Sale");
            
            BigDecimal price = invItem.getSellPrice();
            if (price == null) {
                BigDecimal cost = invItem.getCostPrice();
                price = (cost != null) ? cost.multiply(new BigDecimal("1.5")) : BigDecimal.ZERO;
            }
            
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(qty));
            totalAmount += itemTotal.doubleValue();
            desc.append(invItem.getName()).append(" x").append(qty).append(", ");
        }

        if (desc.length() > 10) {
            desc.setLength(desc.length() - 2);
        }

        // Determine Financial Source Type
        String sourceType = "POS_SALE"; // Default Cash/Card
        if ("BALANCE".equalsIgnoreCase(paymentMethod)) {
            // Deduct Balance
            membershipService.deductBalance(member.getUserId(), totalAmount);
            sourceType = "POS_SALE_BALANCE";
        } else {
            // Assume Cash/Card -> Create external payment record
            financeService.processPayment(
                member.getUserId(), 
                totalAmount, 
                desc.toString()
            );
        }
        
        // Accounting Voucher (Business-Finance Integration)
        financeService.createVoucher(sourceType, member.getId(), BigDecimal.valueOf(totalAmount), desc.toString(), "CN");
        
        // Membership Points
        return membershipService.simulatePurchase(member.getUserId(), (int)totalAmount);
    }
}
