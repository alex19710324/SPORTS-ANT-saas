package com.sportsant.saas.hq.service;

import com.sportsant.saas.ai.service.AiAware;
import com.sportsant.saas.franchise.entity.FranchiseApplication;
import com.sportsant.saas.franchise.repository.FranchiseApplicationRepository;
import com.sportsant.saas.store.entity.Store;
import com.sportsant.saas.store.repository.StoreRepository;
import com.sportsant.saas.communication.service.NotificationService;
import com.sportsant.saas.communication.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HQService implements AiAware {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FranchiseApplicationRepository franchiseApplicationRepository;

    @Autowired
    private NotificationService notificationService;

    public Map<String, Object> getGlobalOverview() {
        List<Store> stores = storeRepository.findAll();
        BigDecimal totalRevenue = stores.stream().map(Store::getTodayRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalVisitors = stores.stream().mapToInt(Store::getTodayVisitors).sum();
        
        Map<String, Object> data = new HashMap<>();
        data.put("totalRevenue", totalRevenue);
        data.put("totalVisitors", totalVisitors);
        data.put("storeCount", stores.size());
        data.put("activeKoc", 1500); // Mock
        return data;
    }

    public List<Store> getStoreMapData() {
        return storeRepository.findAll();
    }

    public List<FranchiseApplication> getFranchiseApplications() {
        return franchiseApplicationRepository.findAll();
    }

    public FranchiseApplication submitFranchiseApplication(FranchiseApplication app) {
        return franchiseApplicationRepository.save(app);
    }

    public FranchiseApplication approveFranchise(Long appId, boolean approve, String comments) {
        return franchiseApplicationRepository.findById(appId).map(app -> {
            app.setStatus(approve ? "APPROVED" : "REJECTED");
            app.setComments(comments);
            
            if (approve) {
                // Auto-create store placeholder
                Store newStore = new Store();
                newStore.setName("New Store - " + app.getApplicantName());
                newStore.setCity(app.getProposedCity());
                storeRepository.save(newStore);
            }
            
            // Notify Applicant (For MVP, we use sendToUser if they have an account, or mock external notification)
            try {
                // Assuming contactInfo is email and we don't have a User object yet for external applicants
                // In a real system, we'd use an EmailService here. 
                // For this MVP's NotificationService which requires a User entity or Role:
                // We will just log it or send to a system admin about the approval
                notificationService.sendToRole(
                    "ROLE_ADMIN",
                    "Franchise Application Processed",
                    "Application for " + app.getApplicantName() + " in " + app.getProposedCity() + " has been " + (approve ? "APPROVED" : "REJECTED"),
                    approve ? "SUCCESS" : "WARNING",
                    "/hq/franchise"
                );
            } catch (Exception e) {
                System.err.println("Failed to notify applicant: " + e.getMessage());
            }

            return franchiseApplicationRepository.save(app);
        }).orElseThrow(() -> new RuntimeException("Application not found"));
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        // e.g., AI suggests "High Churn in Zone Asia"
    }
}
