package com.sportsant.saas.hq.service;

import com.sportsant.saas.ai.service.AiBrainService;
import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.franchise.entity.FranchiseApplication;
import com.sportsant.saas.store.entity.Store;
import com.sportsant.saas.inventory.service.InventoryService;
import com.sportsant.saas.maintenance.service.MaintenanceService;
import com.sportsant.saas.marketing.service.MarketingService;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sportsant.saas.store.repository.StoreRepository;

@Service
public class HQService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MarketingService marketingService;

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AiBrainService aiBrainService;

    public Map<String, Object> getGlobalOverview() {
        return getExecutiveDashboard();
    }

    public List<Store> getStoreMapData() {
        List<Store> stores = storeRepository.findAll();
        // If empty, return some mocks for demo
        if (stores.isEmpty()) {
            return mockStores();
        }
        return stores;
    }

    private List<Store> mockStores() {
        Store s1 = new Store(); s1.setId(1L); s1.setName("Beijing Flagship"); s1.setCity("Beijing"); s1.setLatitude(39.9042); s1.setLongitude(116.4074); s1.setStatus("NORMAL");
        Store s2 = new Store(); s2.setId(2L); s2.setName("Shanghai Store"); s2.setCity("Shanghai"); s2.setLatitude(31.2304); s2.setLongitude(121.4737); s2.setStatus("WARNING");
        return List.of(s1, s2);
    }

    public List<FranchiseApplication> getFranchiseApplications() {
        return Collections.emptyList(); // Mock
    }

    public FranchiseApplication approveFranchise(Long appId, boolean approved, String reason) {
        FranchiseApplication app = new FranchiseApplication();
        app.setId(appId);
        app.setStatus(approved ? "APPROVED" : "REJECTED");
        return app;
    }

    public Map<String, Object> getExecutiveDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // 1. Finance (This Month)
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end = LocalDate.now();
        Map<String, Object> finance = financeService.getFinancialStatement(start, end);
        dashboard.put("finance", finance);

        // 2. Membership
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.findByStatus("ACTIVE").size();
        dashboard.put("members", Map.of(
            "total", totalMembers,
            "active", activeMembers,
            "newThisMonth", 124 // Mock new growth
        ));

        // 3. Operations
        int pendingTasks = maintenanceService.getPendingOrders().size();
        dashboard.put("ops", Map.of(
            "pendingMaintenance", pendingTasks,
            "systemStatus", pendingTasks > 5 ? "WARNING" : "HEALTHY"
        ));

        // 4. Marketing
        long activeCampaigns = marketingService.getAllCampaigns().stream()
                .filter(c -> "ACTIVE".equals(c.getStatus())).count();
        dashboard.put("marketing", Map.of(
            "activeCampaigns", activeCampaigns
        ));

        // 5. Inventory
        long lowStock = inventoryService.getAllItems().stream()
                .filter(i -> i.getQuantity() <= i.getSafetyStock()).count();
        dashboard.put("inventory", Map.of(
            "lowStockItems", lowStock
        ));

        // 6. Live Feed (Mock recent events)
        dashboard.put("liveFeed", List.of(
            Map.of("time", "10:05", "msg", "New Member Joined: John Doe", "type", "info"),
            Map.of("time", "10:02", "msg", "Sale: ¥150 (POS-001)", "type", "success"),
            Map.of("time", "09:55", "msg", "Low Stock Alert: Water 500ml", "type", "warning"),
            Map.of("time", "09:30", "msg", "Maintenance Completed: Treadmill 3", "type", "success")
        ));
        
        // 7. AI Suggestions
        // Note: HQ Dashboard now fetches from AiController for consistency, 
        // or we can delegate here if we want a single endpoint.
        // For now, let's keep it simple and just return empty here, 
        // as the frontend calls /hq/dashboard and /api/ai/suggestions separately
        // or we can embed it:
        try {
            // Trigger analysis (In real life, async)
            // aiBrainService.analyzeBusinessHealth(); // Moved to AiController/Service lazy load
            dashboard.put("aiSuggestions", aiBrainService.getPendingSuggestions());
        } catch (Exception e) {
            dashboard.put("aiSuggestions", Collections.emptyList());
        }

        return dashboard;
    }
}
