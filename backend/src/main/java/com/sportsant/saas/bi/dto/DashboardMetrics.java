package com.sportsant.saas.bi.dto;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardMetrics {
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal netProfit;
    
    private Integer newLeads;
    private Integer newMembers;
    private Double leadConversionRate;
    
    private Integer openTickets;
    private Double ticketSlaCompliance;
    
    private Integer activeCampaigns;
    private BigDecimal marketingRoi;
    
    private Map<String, Integer> revenueByChannel; // e.g., "POS": 5000, "Online": 3000

    // Getters and Setters
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public BigDecimal getNetProfit() { return netProfit; }
    public void setNetProfit(BigDecimal netProfit) { this.netProfit = netProfit; }
    public Integer getNewLeads() { return newLeads; }
    public void setNewLeads(Integer newLeads) { this.newLeads = newLeads; }
    public Integer getNewMembers() { return newMembers; }
    public void setNewMembers(Integer newMembers) { this.newMembers = newMembers; }
    public Double getLeadConversionRate() { return leadConversionRate; }
    public void setLeadConversionRate(Double leadConversionRate) { this.leadConversionRate = leadConversionRate; }
    public Integer getOpenTickets() { return openTickets; }
    public void setOpenTickets(Integer openTickets) { this.openTickets = openTickets; }
    public Double getTicketSlaCompliance() { return ticketSlaCompliance; }
    public void setTicketSlaCompliance(Double ticketSlaCompliance) { this.ticketSlaCompliance = ticketSlaCompliance; }
    public Integer getActiveCampaigns() { return activeCampaigns; }
    public void setActiveCampaigns(Integer activeCampaigns) { this.activeCampaigns = activeCampaigns; }
    public BigDecimal getMarketingRoi() { return marketingRoi; }
    public void setMarketingRoi(BigDecimal marketingRoi) { this.marketingRoi = marketingRoi; }
    public Map<String, Integer> getRevenueByChannel() { return revenueByChannel; }
    public void setRevenueByChannel(Map<String, Integer> revenueByChannel) { this.revenueByChannel = revenueByChannel; }
}
