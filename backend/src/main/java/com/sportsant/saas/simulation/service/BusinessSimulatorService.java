package com.sportsant.saas.simulation.service;

import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.inventory.service.SmartReplenishmentService;
import com.sportsant.saas.iot.service.PredictiveMaintenanceService;
import com.sportsant.saas.marketing.service.AutoRetentionService;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import com.sportsant.saas.simulation.dto.SimulationEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class BusinessSimulatorService {

    private final MemberRepository memberRepository;
    private final DeviceRepository deviceRepository;
    private final AutoRetentionService retentionService;
    private final PredictiveMaintenanceService maintenanceService;
    private final SmartReplenishmentService replenishmentService;
    private final FinanceService financeService;

    private boolean isRunning = false;
    private final Random random = new Random();
    
    // Store recent events for frontend polling (Circular buffer conceptually)
    private final List<SimulationEvent> eventLog = new CopyOnWriteArrayList<>();

    public BusinessSimulatorService(MemberRepository memberRepository,
                                    DeviceRepository deviceRepository,
                                    AutoRetentionService retentionService,
                                    PredictiveMaintenanceService maintenanceService,
                                    SmartReplenishmentService replenishmentService,
                                    FinanceService financeService) {
        this.memberRepository = memberRepository;
        this.deviceRepository = deviceRepository;
        this.retentionService = retentionService;
        this.maintenanceService = maintenanceService;
        this.replenishmentService = replenishmentService;
        this.financeService = financeService;
    }

    public void startSimulation() {
        this.isRunning = true;
        addEvent("OPEN_CLAW", "OpenClaw Commander 接管系统", "全自动运营引擎已启动，大龙虾正在指挥...", "SUCCESS", "SYSTEM");
    }

    public void stopSimulation() {
        this.isRunning = false;
        addEvent("OPEN_CLAW", "OpenClaw Commander 暂停系统", "等待人工指令", "WARNING", "SYSTEM");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public List<SimulationEvent> getRecentEvents() {
        // Return last 50 events
        int size = eventLog.size();
        return eventLog.subList(Math.max(0, size - 50), size);
    }

    private void addEvent(String type, String title, String description, String level, String relatedId) {
        SimulationEvent event = new SimulationEvent(type, title, description, level, relatedId);
        eventLog.add(event);
        if (eventLog.size() > 200) {
            eventLog.remove(0); // Keep buffer manageable
        }
    }

    @Scheduled(fixedRate = 3000) // Every 3 seconds trigger a random event
    public void runSimulationLoop() {
        if (!isRunning) return;

        int eventType = random.nextInt(4); // 0: Member, 1: Purchase, 2: Device, 3: Retention

        try {
            switch (eventType) {
                case 0: simulateMemberCheckIn(); break;
                case 1: simulatePurchase(); break;
                case 2: simulateDeviceTelemetry(); break;
                case 3: simulateChurnCheck(); break;
            }
        } catch (Exception e) {
            addEvent("ERROR", "模拟异常", e.getMessage(), "ERROR", "SYSTEM");
        }
    }

    private void simulateMemberCheckIn() {
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) return;
        
        Member member = members.get(random.nextInt(members.size()));
        addEvent("MEMBER", "会员进场", member.getName() + " 刷脸入场成功", "INFO", String.valueOf(member.getId()));
        
        // Randomly simulate low balance alert
        if (random.nextDouble() < 0.2) {
             addEvent("FINANCE", "余额不足提醒", "会员 " + member.getName() + " 余额低于 50 元", "WARNING", String.valueOf(member.getId()));
        }
    }

    private void simulatePurchase() {
        // Mock purchase
        String[] products = {"运动饮料", "蛋白棒", "毛巾", "私教课"};
        String product = products[random.nextInt(products.length)];
        BigDecimal amount = BigDecimal.valueOf(10 + random.nextInt(100));
        
        financeService.createVoucher("POS", 0L, amount, "自动售货: " + product);
        addEvent("PURCHASE", "商品售出", "售出 " + product + "，金额 ¥" + amount, "SUCCESS", "POS");
        
        // Trigger replenishment check randomly
        if (random.nextDouble() < 0.1) {
            addEvent("INVENTORY", "触发自动补货", "检测到库存变动，OpenClaw 正在计算补货量...", "INFO", "SYSTEM");
            replenishmentService.getUrgentReplenishments("DEFAULT"); // Just to trigger logic
        }
    }

    private void simulateDeviceTelemetry() {
        List<Device> devices = deviceRepository.findAll();
        if (devices.isEmpty()) return;
        
        Device device = devices.get(random.nextInt(devices.size()));
        
        // Simulate a "heartbeat" or anomaly
        if (random.nextDouble() < 0.1) {
            // Simulate anomaly
            device.setTemperature(85.0); // Overheat!
            deviceRepository.save(device);
            addEvent("DEVICE", "设备异常遥测", device.getName() + " 温度过高 (85°C)!", "ERROR", String.valueOf(device.getId()));
            
            // Trigger maintenance check immediately
            maintenanceService.analyzeDeviceHealth(); 
            addEvent("IOT", "自动生成工单", "OpenClaw 已为 " + device.getName() + " 生成紧急维修工单", "WARNING", String.valueOf(device.getId()));
        } else {
            addEvent("DEVICE", "设备健康检查", device.getName() + " 运行正常，健康分 " + device.getHealthScore(), "INFO", String.valueOf(device.getId()));
        }
    }

    private void simulateChurnCheck() {
        // Simulate AI detecting a risky member
        if (random.nextDouble() < 0.05) {
            List<Member> members = memberRepository.findAll();
            if (members.isEmpty()) return;
            Member member = members.get(random.nextInt(members.size()));
            
            addEvent("AI", "流失风险预警", "检测到会员 " + member.getName() + " 30天未到访，风险等级 HIGH", "WARNING", String.valueOf(member.getId()));
            
            // Auto retention
            try {
                retentionService.executeRetentionCampaign(String.valueOf(member.getId()), "DEFAULT");
                addEvent("MARKETING", "自动挽回执行", "OpenClaw 已向 " + member.getName() + " 发送优惠券和召回短信", "SUCCESS", String.valueOf(member.getId()));
            } catch (Exception e) {
                // Ignore errors in simulation
            }
        }
    }
}
