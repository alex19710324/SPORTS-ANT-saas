package com.sportsant.saas.globalization.controller;

import com.sportsant.saas.common.context.UserContext;
import com.sportsant.saas.globalization.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/globalization/capability")
@Tag(name = "国际化能力", description = "提供全球化基础服务，包括税务、短信、验证、格式化、合规文档等")
public class GlobalizationCapabilityController {

    @Autowired
    private TaxEngine taxEngine;

    @Autowired
    private CommunicationGateway communicationGateway;

    @Autowired
    private ComplianceValidator complianceValidator;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private TimezoneService timezoneService;

    @Autowired
    private LegalComplianceService legalComplianceService;

    private String getEffectiveLocale(String locale) {
        if (locale != null && !locale.isEmpty()) return locale;
        return UserContext.getLocale();
    }

    @PostMapping("/tax")
    public TaxEngine.TaxResult calculateTax(@RequestParam(required = false) String locale, @RequestParam BigDecimal amount) {
        return taxEngine.calculateTax(getEffectiveLocale(locale), amount);
    }

    @PostMapping("/sms")
    public String sendSms(@RequestParam(required = false) String locale, @RequestParam String phone, @RequestParam String message) {
        return communicationGateway.sendSms(getEffectiveLocale(locale), phone, message);
    }

    @PostMapping("/validate/phone")
    public ComplianceValidator.ValidationResult validatePhone(@RequestParam(required = false) String locale, @RequestParam String phone) {
        return complianceValidator.validatePhone(getEffectiveLocale(locale), phone);
    }

    @PostMapping("/validate/postal")
    public ComplianceValidator.ValidationResult validatePostal(@RequestParam(required = false) String locale, @RequestParam String postal) {
        return complianceValidator.validatePostalCode(getEffectiveLocale(locale), postal);
    }

    @PostMapping("/validate/id")
    public ComplianceValidator.ValidationResult validateId(@RequestParam(required = false) String locale, @RequestParam String id) {
        return complianceValidator.validateIdCard(getEffectiveLocale(locale), id);
    }

    @PostMapping("/format/currency")
    public String formatCurrency(@RequestParam(required = false) String locale, @RequestParam BigDecimal amount) {
        return currencyService.formatCurrency(amount, getEffectiveLocale(locale));
    }

    @PostMapping("/format/date")
    public String formatDate(@RequestParam(required = false) String locale, 
                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                             @RequestParam(defaultValue = "date") String formatKey) {
        return timezoneService.formatTimeForLocale(date, getEffectiveLocale(locale), formatKey);
    }

    @GetMapping("/compliance/documents")
    public List<LegalComplianceService.LegalDocument> getLegalDocuments(@RequestParam(required = false) String locale) {
        return legalComplianceService.getRequiredDocuments(getEffectiveLocale(locale));
    }
}
