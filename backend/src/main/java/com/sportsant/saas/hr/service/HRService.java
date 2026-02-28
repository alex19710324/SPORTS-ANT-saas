package com.sportsant.saas.hr.service;

import com.sportsant.saas.entity.ERole;
import com.sportsant.saas.entity.Role;
import com.sportsant.saas.entity.User;
import com.sportsant.saas.repository.UserRepository;
import com.sportsant.saas.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HRService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<Map<String, Object>> getStaffList() {
        // Fetch users who are NOT just ROLE_USER (which are members)
        // For MVP, we'll iterate all users and filter by roles that are staff-like
        List<User> allUsers = userRepository.findAll();
        
        List<Map<String, Object>> staffList = new ArrayList<>();
        
        for (User user : allUsers) {
            boolean isStaff = user.getRoles().stream()
                .anyMatch(r -> !r.getName().equals(ERole.ROLE_USER));
            
            if (isStaff) {
                Map<String, Object> staff = new HashMap<>();
                staff.put("id", user.getId());
                staff.put("username", user.getUsername());
                staff.put("email", user.getEmail());
                staff.put("roles", user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()));
                
                // Mock Performance Data
                staff.put("attendance", 95 + (int)(Math.random() * 5)); // 95-100%
                staff.put("sales", BigDecimal.valueOf(Math.random() * 50000).intValue());
                staff.put("rating", 4.0 + Math.random()); // 4.0 - 5.0
                
                staffList.add(staff);
            }
        }
        return staffList;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> calculatePayroll() {
        // Mock Payroll Calculation
        List<Map<String, Object>> staffList = getStaffList();
        BigDecimal totalBaseSalary = BigDecimal.ZERO;
        BigDecimal totalBonus = BigDecimal.ZERO;

        List<Map<String, Object>> details = new ArrayList<>();

        for (Map<String, Object> staff : staffList) {
            BigDecimal base = BigDecimal.valueOf(5000); // Base salary
            BigDecimal sales = BigDecimal.valueOf((Integer) staff.get("sales"));
            BigDecimal bonus = sales.multiply(BigDecimal.valueOf(0.05)); // 5% commission
            
            totalBaseSalary = totalBaseSalary.add(base);
            totalBonus = totalBonus.add(bonus);
            
            Map<String, Object> detail = new HashMap<>();
            detail.put("name", staff.get("username"));
            detail.put("base", base);
            detail.put("bonus", bonus);
            detail.put("total", base.add(bonus));
            details.add(detail);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalPayroll", totalBaseSalary.add(totalBonus));
        result.put("breakdown", details);
        return result;
    }
}
