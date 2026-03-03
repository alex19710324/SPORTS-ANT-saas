package com.sportsant.saas.crm.service;

import com.sportsant.saas.common.context.UserContext;
import com.sportsant.saas.crm.entity.Lead;
import com.sportsant.saas.crm.repository.LeadRepository;
import com.sportsant.saas.globalization.service.ComplianceValidator;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CRMService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private MembershipService membershipService;
    
    @Autowired
    private ComplianceValidator complianceValidator;

    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    public Lead createLead(Lead lead) {
        // Auto-detect context
        if (lead.getLocale() == null) {
            lead.setLocale(UserContext.getLocale());
        }
        if (lead.getTimezone() == null) {
            lead.setTimezone(UserContext.getTimezone().getId());
        }
        
        // Validate Phone
        if (lead.getPhone() != null) {
            ComplianceValidator.ValidationResult res = complianceValidator.validatePhone(lead.getLocale(), lead.getPhone());
            if (!res.valid) {
                throw new RuntimeException("Invalid phone number: " + res.message);
            }
        }

        // Auto-Score based on source
        if ("REFERRAL".equals(lead.getSource())) {
            lead.setScore(50); // High intent
        } else {
            lead.setScore(10);
        }
        return leadRepository.save(lead);
    }

    @Transactional
    public Member convertLeadToMember(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        if ("CONVERTED".equals(lead.getStatus())) {
            throw new RuntimeException("Lead already converted");
        }

        // Generate a placeholder UserID or link to real user system
        // For MVP, using simplified ID generation strategy as before
        Long newUserId = System.currentTimeMillis(); 
        
        // Delegate to MembershipService for consistent creation logic (Compliance, Defaults, etc.)
        Member savedMember = membershipService.createMember(
            newUserId,
            lead.getName(),
            lead.getPhone(),
            null, // ID Card not collected at lead stage
            lead.getLocale()
        );

        // Update Lead Status
        lead.setStatus("CONVERTED");
        lead.setNotes((lead.getNotes() == null ? "" : lead.getNotes()) + "\nConverted to Member ID: " + savedMember.getId());
        leadRepository.save(lead);

        return savedMember;
    }

    public Lead updateLeadStatus(Long leadId, String status, String notes) {
        Lead lead = leadRepository.findById(leadId).orElseThrow();
        lead.setStatus(status);
        if (notes != null) {
            lead.setNotes((lead.getNotes() == null ? "" : lead.getNotes()) + "\n" + LocalDateTime.now() + ": " + notes);
        }
        return leadRepository.save(lead);
    }
}
