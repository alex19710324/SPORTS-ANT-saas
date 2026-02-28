package com.sportsant.saas.hq.service;

import com.sportsant.saas.data.service.AnalyticsService;
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

@Service
public class HQService {

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
    private AnalyticsService analyticsService;

    public Map<String, Object> getGlobalOverview() {
        return getExecutiveDashboard();
    }

    public List<Store> getStoreMapData() {
        return Collections.emptyList(); // Mock
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
        int pendingTasks = maintenanceService.getPendingTasks().size();
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
                .filter(i -> i.getQuantity() <= i.getReorderPoint()).count();
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

        return dashboard;
    }
}
