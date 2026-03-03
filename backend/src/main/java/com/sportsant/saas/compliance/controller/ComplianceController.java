package com.sportsant.saas.compliance.controller;

import com.sportsant.saas.compliance.entity.Consent;
import com.sportsant.saas.compliance.service.ComplianceService;
import com.sportsant.saas.compliance.service.ConsentService;
import com.sportsant.saas.dto.ConsentDto;
import com.sportsant.saas.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

    @Autowired
    private ConsentService consentService;

    @Autowired
    private ComplianceService complianceService;

    @GetMapping("/consents")
    public ResponseEntity<List<Consent>> getMyConsents() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(consentService.getHistory(userDetails.getId()));
    }

    @PostMapping("/consents")
    public ResponseEntity<?> updateConsent(@RequestBody ConsentDto consentDto, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        
        consentService.recordConsent(userDetails.getId(), consentDto.getAgreementType(), consentDto.getVersion(), consentDto.isAgreed(), ip, ua);
        return ResponseEntity.ok("Consent updated");
    }

    @PostMapping("/export")
    public ResponseEntity<?> requestDataExport(HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        try {
            String jsonData = complianceService.exportUserData(userDetails.getId(), request.getRemoteAddr());
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"my_data_export.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/forget")
    public ResponseEntity<?> requestRightToBeForgotten(@RequestBody(required = false) Map<String, String> payload, HttpServletRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String reason = payload != null ? payload.getOrDefault("reason", "User request") : "User request";
        
        try {
            complianceService.deleteAccount(userDetails.getId(), reason, request.getRemoteAddr());
            return ResponseEntity.ok(Map.of("message", "Account successfully anonymized and scheduled for deletion."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
