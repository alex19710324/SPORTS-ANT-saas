package com.sportsant.saas.openclaw.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class OpenClawDashboardDTO {
    
    private ArchitectView architectView;
    private BusinessView businessView;
    private DeveloperView developerView;
    private String openClawMessage; // 龙虾哥的实时指令
    private List<String> executionLogs; // 实时执行日志
    private List<DevChatMessage> devChatMessages; // Trae x OpenClaw 对话
    private String systemVersion; // 自动进化版本号
    
    // Getters and Setters
    public ArchitectView getArchitectView() { return architectView; }
    public void setArchitectView(ArchitectView architectView) { this.architectView = architectView; }
    public BusinessView getBusinessView() { return businessView; }
    public void setBusinessView(BusinessView businessView) { this.businessView = businessView; }
    public DeveloperView getDeveloperView() { return developerView; }
    public void setDeveloperView(DeveloperView developerView) { this.developerView = developerView; }
    public String getOpenClawMessage() { return openClawMessage; }
    public void setOpenClawMessage(String openClawMessage) { this.openClawMessage = openClawMessage; }
    public List<String> getExecutionLogs() { return executionLogs; }
    public void setExecutionLogs(List<String> executionLogs) { this.executionLogs = executionLogs; }
    public List<DevChatMessage> getDevChatMessages() { return devChatMessages; }
    public void setDevChatMessages(List<DevChatMessage> devChatMessages) { this.devChatMessages = devChatMessages; }
    public String getSystemVersion() { return systemVersion; }
    public void setSystemVersion(String systemVersion) { this.systemVersion = systemVersion; }

    public static class DevChatMessage {
        private String sender; // "OpenClaw" or "Trae"
        private String content;
        private String timestamp;
        
        public DevChatMessage() {}
        public DevChatMessage(String sender, String content, String timestamp) {
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
        }
        
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }

    public static class ArchitectView {
        private double systemHealth; // 0-100
        private double apiLatencyMs;
        private double errorRate; // %
        private int activeMicroservices;
        private String architectureStatus; // STABLE, DEGRADED
        private List<String> warnings;

        public double getSystemHealth() { return systemHealth; }
        public void setSystemHealth(double systemHealth) { this.systemHealth = systemHealth; }
        public double getApiLatencyMs() { return apiLatencyMs; }
        public void setApiLatencyMs(double apiLatencyMs) { this.apiLatencyMs = apiLatencyMs; }
        public double getErrorRate() { return errorRate; }
        public void setErrorRate(double errorRate) { this.errorRate = errorRate; }
        public int getActiveMicroservices() { return activeMicroservices; }
        public void setActiveMicroservices(int activeMicroservices) { this.activeMicroservices = activeMicroservices; }
        public String getArchitectureStatus() { return architectureStatus; }
        public void setArchitectureStatus(String architectureStatus) { this.architectureStatus = architectureStatus; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    }

    public static class BusinessView {
        private double currentRevenue;
        private double projectedRevenue;
        private int activeUsers;
        private double growthRate; // %
        private String keyInsight;

        public double getCurrentRevenue() { return currentRevenue; }
        public void setCurrentRevenue(double currentRevenue) { this.currentRevenue = currentRevenue; }
        public double getProjectedRevenue() { return projectedRevenue; }
        public void setProjectedRevenue(double projectedRevenue) { this.projectedRevenue = projectedRevenue; }
        public int getActiveUsers() { return activeUsers; }
        public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
        public double getGrowthRate() { return growthRate; }
        public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }
        public String getKeyInsight() { return keyInsight; }
        public void setKeyInsight(String keyInsight) { this.keyInsight = keyInsight; }
    }

    public static class DeveloperView {
        private int pendingBugs;
        private int commitsToday;
        private String lastBuildStatus; // SUCCESS, FAILURE
        private String currentTask;
        private double codeQualityScore; // 0-100

        public int getPendingBugs() { return pendingBugs; }
        public void setPendingBugs(int pendingBugs) { this.pendingBugs = pendingBugs; }
        public int getCommitsToday() { return commitsToday; }
        public void setCommitsToday(int commitsToday) { this.commitsToday = commitsToday; }
        public String getLastBuildStatus() { return lastBuildStatus; }
        public void setLastBuildStatus(String lastBuildStatus) { this.lastBuildStatus = lastBuildStatus; }
        public String getCurrentTask() { return currentTask; }
        public void setCurrentTask(String currentTask) { this.currentTask = currentTask; }
        public double getCodeQualityScore() { return codeQualityScore; }
        public void setCodeQualityScore(double codeQualityScore) { this.codeQualityScore = codeQualityScore; }
    }
}
