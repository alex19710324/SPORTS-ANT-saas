package com.sportsant.saas.bi.service;

import com.sportsant.saas.bi.dto.DashboardMetrics;
import com.sportsant.saas.crm.entity.Lead;
import com.sportsant.saas.crm.repository.LeadRepository;
// import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.marketing.entity.Campaign;
import com.sportsant.saas.marketing.repository.CampaignRepository;
import com.sportsant.saas.workflow.entity.WorkOrder;
import com.sportsant.saas.workflow.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    // @Autowired
    // private FinanceService financeService;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    public DashboardMetrics getOverallMetrics() {
        DashboardMetrics metrics = new DashboardMetrics();

        // 1. Finance (Mocked as FinanceService logic is complex)
        metrics.setTotalRevenue(new BigDecimal("50000.00")); // Mock
        metrics.setTotalCost(new BigDecimal("30000.00")); // Mock
        metrics.setNetProfit(metrics.getTotalRevenue().subtract(metrics.getTotalCost()));

        // 2. CRM
        List<Lead> leads = leadRepository.findAll();
        long newLeads = leads.stream().filter(l -> "NEW".equals(l.getStatus())).count();
        long converted = leads.stream().filter(l -> "CONVERTED".equals(l.getStatus())).count();
        
        metrics.setNewLeads((int) newLeads);
        metrics.setNewMembers((int) converted);
        if (leads.size() > 0) {
            metrics.setLeadConversionRate((double) converted / leads.size() * 100);
        } else {
            metrics.setLeadConversionRate(0.0);
        }

        // 3. Operations
        List<WorkOrder> openTickets = workOrderRepository.findByStatusNot("RESOLVED");
        metrics.setOpenTickets(openTickets.size());
        
        // Mock SLA Compliance
        metrics.setTicketSlaCompliance(95.5);

        // 4. Marketing
        List<Campaign> activeCampaigns = campaignRepository.findByStatus("ACTIVE");
        metrics.setActiveCampaigns(activeCampaigns.size());
        
        // Avg ROI
        double totalRoi = activeCampaigns.stream()
                .mapToDouble(c -> c.getRoi() != null ? c.getRoi().doubleValue() : 0)
                .sum();
        if (activeCampaigns.size() > 0) {
            metrics.setMarketingRoi(BigDecimal.valueOf(totalRoi / activeCampaigns.size()));
        } else {
            metrics.setMarketingRoi(BigDecimal.ZERO);
        }

        // 5. Revenue Channel
        Map<String, Integer> channels = new HashMap<>();
        channels.put("POS", 35000);
        channels.put("Online", 15000);
        metrics.setRevenueByChannel(channels);

        return metrics;
    }
}
