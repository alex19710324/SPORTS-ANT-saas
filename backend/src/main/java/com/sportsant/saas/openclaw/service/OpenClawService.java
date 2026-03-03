package com.sportsant.saas.openclaw.service;

import com.sportsant.saas.openclaw.dto.OpenClawDashboardDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class OpenClawService {

    private final Random random = new Random();
    private int operationCounter = 0;
    
    // System Version State
    private int majorVersion = 1;
    private int minorVersion = 0;
    private int patchVersion = 0;

    // Shared Memory Log (Simulated)
    private static final List<String> JOINT_DEV_LOGS = new java.util.concurrent.CopyOnWriteArrayList<>();
    // Shared Chat History
    private static final List<OpenClawDashboardDTO.DevChatMessage> CHAT_HISTORY = new java.util.concurrent.CopyOnWriteArrayList<>();
    
    public void incrementVersion(String type) {
        if ("MAJOR".equals(type)) {
            majorVersion++;
            minorVersion = 0;
            patchVersion = 0;
        } else if ("MINOR".equals(type)) {
            minorVersion++;
            patchVersion = 0;
        } else {
            patchVersion++;
        }
    }
    
    public String getCurrentVersion() {
        return "v" + majorVersion + "." + minorVersion + "." + patchVersion;
    }
    
    public void addJointDevLog(String log) {
        JOINT_DEV_LOGS.add(log);
        if (JOINT_DEV_LOGS.size() > 50) {
            JOINT_DEV_LOGS.remove(0);
        }
    }
    
    public void addChatMessage(String sender, String content) {
        String time = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        CHAT_HISTORY.add(new OpenClawDashboardDTO.DevChatMessage(sender, content, time));
        if (CHAT_HISTORY.size() > 20) {
            CHAT_HISTORY.remove(0);
        }
    }

    public OpenClawDashboardDTO getDashboardData() {
        OpenClawDashboardDTO dto = new OpenClawDashboardDTO();
        operationCounter++;

        // ... (existing logic) ...
        // 1. 架构师视角 (Architect View)
        OpenClawDashboardDTO.ArchitectView archView = new OpenClawDashboardDTO.ArchitectView();
        double health = 95 + random.nextDouble() * 5;
        // Simulate a dip in health occasionally
        if (operationCounter % 10 == 0) {
            health = 85.0; // Warning level
        }
        archView.setSystemHealth(health);
        archView.setApiLatencyMs(20 + random.nextDouble() * 50); 
        archView.setErrorRate(random.nextDouble() * 0.1);
        archView.setActiveMicroservices(12);
        archView.setArchitectureStatus(health > 90 ? "STABLE" : "OPTIMIZING");
        archView.setWarnings(List.of());
        dto.setArchitectView(archView);

        // 2. CEO 视角 (Business View)
        OpenClawDashboardDTO.BusinessView businessView = new OpenClawDashboardDTO.BusinessView();
        businessView.setCurrentRevenue(125000 + operationCounter * 10 + random.nextInt(50));
        businessView.setProjectedRevenue(138000 + random.nextInt(500));
        businessView.setActiveUsers(8540 + operationCounter + random.nextInt(5));
        businessView.setGrowthRate(12.5);
        businessView.setKeyInsight("周末流量激增，建议扩容推荐服务节点");
        dto.setBusinessView(businessView);

        // 3. 资深程序员视角 (Developer View)
        OpenClawDashboardDTO.DeveloperView devView = new OpenClawDashboardDTO.DeveloperView();
        devView.setPendingBugs(Math.max(0, 2 - (operationCounter / 5))); // Auto fixing bugs!
        devView.setCommitsToday(15 + operationCounter);
        devView.setLastBuildStatus("SUCCESS");
        devView.setCurrentTask("Refactoring Membership Module");
        devView.setCodeQualityScore(98.5 + (operationCounter * 0.01));
        dto.setDeveloperView(devView);

        // 4. 龙虾哥指令
        dto.setOpenClawMessage(getRandomMessage());
        
        // 5. 实时日志 (Merge System Logs + Joint Dev Logs)
        List<String> combinedLogs = new java.util.ArrayList<>(generateExecutionLogs());
        // Add last 3 joint dev logs if any
        int size = JOINT_DEV_LOGS.size();
        if (size > 0) {
            combinedLogs.addAll(JOINT_DEV_LOGS.subList(Math.max(0, size - 3), size));
        }
        dto.setExecutionLogs(combinedLogs);
        
        // 6. 联合开发对话
        dto.setDevChatMessages(new java.util.ArrayList<>(CHAT_HISTORY));
        
        // 7. System Version
        dto.setSystemVersion(getCurrentVersion());

        return dto;
    }

    private List<String> generateExecutionLogs() {
        List<String> logs = new java.util.ArrayList<>();
        String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        String[] actions = {
            "正在优化 InventoryService 数据库索引...",
            "检测到美国东部节点延迟 (45ms)，自动切换路由...",
            "自动扩容: 增加 2 个 Redis 缓存节点...",
            "安全扫描: 拦截 3 次可疑 SQL 注入攻击...",
            "代码审查: 发现冗余代码，已自动重构...",
            "CEO 简报生成中: 本小时营收增长 1.2%...",
            "清理过期缓存对象: 1,024 个...",
            "同步全球库存数据 (CN <-> US)..."
        };
        
        logs.add("[" + timestamp + "] [INFO] " + actions[random.nextInt(actions.length)]);
        if (random.nextBoolean()) {
            logs.add("[" + timestamp + "] [DEBUG] 内存使用率正常 (45%)");
        }
        return logs;
    }

    private String getRandomMessage() {
        String[] messages = {
            "架构检测完毕，服务健康度 99.8%。继续保持。",
            "CEO 提醒：本月营收目标即将达成，注意观察流量峰值。",
            "资深程序员建议：InventoryService 代码复杂度略高，建议重构。",
            "全系统运行平稳，您可以喝杯咖啡了。",
            "正在进行自动代码审查...通过。",
            "微服务链路追踪正常，无慢查询。",
            "检测到潜在的并发风险，已自动启用限流策略。"
        };
        return messages[random.nextInt(messages.length)];
    }

    public String performHandshake() {
        return "Trae Protocol v1.0 <-> OpenClaw Command Center: Connection Established. 🦞🤝🤖";
    }

    public String triggerEvent(String type) {
        String message = "";
        switch (type) {
            case "ATTACK":
                message = "Simulating DDoS Attack... OpenClaw Shield Activated!";
                addJointDevLog("[SECURITY] DETECTED HIGH TRAFFIC ANOMALY. ACTIVATING FIREWALL.");
                addChatMessage("OpenClaw", "Trae, we are under attack! 10k req/sec from unknown IP.");
                addChatMessage("Trae AI", "Deploying CloudFlare mitigation rules. Rerouting traffic.");
                break;
            case "MARKETING":
                message = "Boosting Marketing Campaign... Traffic increasing!";
                addJointDevLog("[MARKETING] CAMPAIGN 'GLOBAL_LAUNCH' STARTED.");
                addChatMessage("OpenClaw", "Launch the 'Summer Sale' emails now.");
                addChatMessage("Trae AI", "Emails sent to 50k users. Open rate tracking active.");
                break;
            case "REVENUE":
                message = "Optimizing Revenue Algorithms... Pricing updated.";
                addJointDevLog("[FINANCE] DYNAMIC PRICING ENGINE UPDATED.");
                addChatMessage("OpenClaw", "Adjust blind box prices for high demand regions.");
                addChatMessage("Trae AI", "Prices updated. Projected revenue +5%.");
                break;
            default:
                message = "Event triggered.";
        }
        return message;
    }
}
