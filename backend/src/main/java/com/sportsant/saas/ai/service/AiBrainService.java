package com.sportsant.saas.ai.service;

import com.sportsant.saas.ai.entity.AiSuggestion;
import com.sportsant.saas.ai.repository.AiSuggestionRepository;
import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.inventory.entity.InventoryItem;
import com.sportsant.saas.inventory.repository.InventoryRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AiBrainService {

    @Autowired
    private AiSuggestionRepository suggestionRepository;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Trigger a full analysis of the business state (Finance, Member, Inventory)
     */
    public void analyzeBusinessHealth() {
        analyzeFinancialHealth();
        analyzeMemberHealth();
        analyzeInventoryHealth();
    }

    private void analyzeInventoryHealth() {
        List<InventoryItem> items = inventoryRepository.findAll();
        long lowStockCount = items.stream().filter(i -> i.getQuantity() <= i.getSafetyStock()).count();
        
        // Rule: If > 5 items are low stock, suggest restocking
        if (lowStockCount > 5) {
            proposeSuggestion(
                "Inventory Critical Warning",
                lowStockCount + " items are below safety stock level. Immediate restocking recommended to prevent sales loss.",
                "INVENTORY",
                "HIGH",
                "LINK",
                "{\"route\": \"/inventory\"}"
            );
        }
        
        // Rule: Dead Stock (Items with quantity > 100 but last updated long ago - Mock logic for MVP)
        // In real app, we check transaction logs. Here we just mock based on quantity > 500
        long overstockCount = items.stream().filter(i -> i.getQuantity() > 500).count();
        if (overstockCount > 0) {
            proposeSuggestion(
                "Potential Dead Stock",
                overstockCount + " items have unusually high stock levels (>500). Consider a clearance sale.",
                "MARKETING",
                "MEDIUM",
                "LINK",
                "{\"route\": \"/marketing/campaigns\"}"
            );
        }
    }

    public void analyzeMemberHealth() {
        List<Member> members = memberRepository.findAll();
        long churnRiskCount = 0;
        
        for (Member m : members) {
            if (m.getLastVisitDate() != null) {
                long daysSinceVisit = ChronoUnit.DAYS.between(m.getLastVisitDate(), LocalDateTime.now());
                if (daysSinceVisit > 30) {
                    churnRiskCount++;
                }
            }
        }
        
        if (churnRiskCount > 10) { 
             proposeSuggestion(
                "High Member Churn Risk",
                churnRiskCount + " members haven't visited in 30+ days. Launch a retention campaign.",
                "MEMBERSHIP",
                "HIGH",
                "API",
                "{\"endpoint\": \"/api/marketing/campaigns/retention\", \"method\": \"POST\"}"
            );
        }
    }

    public void analyzeFinancialHealth() {
        Map<String, Object> report = financeService.getFinancialStatement(LocalDate.now().withDayOfMonth(1), LocalDate.now());
        
        Double margin = (Double) report.get("margin");
        Double deferredRevenue = (Double) report.get("deferredRevenue");
        Double cash = (Double) report.get("totalIncome"); 

        // Rule 1: High Liability Risk
        if (deferredRevenue != null && cash != null && cash > 0 && deferredRevenue > cash * 0.8) {
             proposeSuggestion(
                "High Liability Risk",
                "Prepaid liability (¥" + String.format("%.2f", deferredRevenue) + ") is high relative to cash flow. Boost service consumption.",
                "FINANCE",
                "HIGH",
                "API",
                "{\"endpoint\": \"/api/marketing/boost-consumption\", \"method\": \"POST\"}"
            );
        }

        // Rule 2: Low Margin Alert
        if (margin != null && margin < 15.0) {
            proposeSuggestion(
                "Profit Margin Below Target",
                "Current net margin is " + String.format("%.1f", margin) + "%. Review COGS and Payroll.",
                "FINANCE",
                "MEDIUM",
                "LINK",
                "{\"route\": \"/finance/dashboard\"}"
            );
        }
    }

    public List<AiSuggestion> getSuggestions() {
        return getPendingSuggestions();
    }

    public AiSuggestion processFeedback(Long suggestionId, boolean accepted, String feedback) {
        if (accepted) {
            return executeSuggestion(suggestionId);
        } else {
            return ignoreSuggestion(suggestionId);
        }
    }

    @Transactional
    public AiSuggestion proposeSuggestion(String title, String content, String type, String priority, String actionableApi) {
        // Adapt old signature to new one
        // Assume actionableApi is a simple API call if it starts with /api, or LINK otherwise?
        // Or just store it as actionPayload with type LINK for simplicity if unsure
        String actionType = "LINK";
        String payload = "{\"route\": \"" + actionableApi + "\"}";
        
        if (actionableApi != null && actionableApi.startsWith("/api")) {
            actionType = "API";
            payload = "{\"endpoint\": \"" + actionableApi + "\", \"method\": \"POST\"}";
        }
        
        return proposeSuggestion(title, content, type, priority, actionType, payload);
    }

    @Transactional
    public AiSuggestion proposeSuggestion(String title, String content, String type, String priority, String actionType, String actionPayload) {
        // Deduplicate: Don't add if a pending suggestion with same title exists
        List<AiSuggestion> pending = suggestionRepository.findByStatus("PENDING");
        boolean exists = pending.stream().anyMatch(s -> s.getTitle().equals(title));
        if (exists) return null;

        AiSuggestion suggestion = new AiSuggestion();
        suggestion.setTitle(title);
        suggestion.setContent(content);
        suggestion.setType(type);
        suggestion.setPriority(priority);
        suggestion.setActionType(actionType);
        suggestion.setActionPayload(actionPayload);
        suggestion.setStatus("PENDING");
        return suggestionRepository.save(suggestion);
    }

    public List<AiSuggestion> getPendingSuggestions() {
        // Trigger analysis on read (Lazy evaluation for MVP)
        // In prod, this should be async background job
        analyzeBusinessHealth(); 
        return suggestionRepository.findByStatus("PENDING");
    }

    @Transactional
    public AiSuggestion executeSuggestion(Long id) {
        AiSuggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suggestion not found"));
        
        suggestion.setStatus("EXECUTED");
        suggestion.setExecutedAt(LocalDateTime.now());
        
        // In real app, we might invoke the API defined in actionPayload here via RestTemplate
        // For MVP, we just mark as executed
        
        return suggestionRepository.save(suggestion);
    }
    
    @Transactional
    public AiSuggestion ignoreSuggestion(Long id) {
        AiSuggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suggestion not found"));
        suggestion.setStatus("IGNORED");
        return suggestionRepository.save(suggestion);
    }
}
