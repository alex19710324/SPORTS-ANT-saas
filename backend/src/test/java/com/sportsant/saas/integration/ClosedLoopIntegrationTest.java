package com.sportsant.saas.integration;

import com.sportsant.saas.device.entity.Device;
import com.sportsant.saas.device.repository.DeviceRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.service.MembershipService;
import com.sportsant.saas.workbench.service.FrontDeskService;
import com.sportsant.saas.workbench.service.StoreManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class ClosedLoopIntegrationTest {

    @Autowired
    private FrontDeskService frontDeskService;

    @Autowired
    private StoreManagerService storeManagerService;

    @Autowired
    private DeviceRepository deviceRepository;

    @MockBean
    private MembershipService membershipService;

    @Test
    public void testSalesToRevenueLoop() {
        // Setup Member Mock
        Member member = new Member();
        member.setId(1L);
        member.setName("Test Member");
        member.setMemberCode("M888");
        member.setUserId(100L);
        
        when(membershipService.findMemberByCodeOrPhone("M888")).thenReturn(member);
        when(membershipService.simulatePurchase(anyLong(), anyInt())).thenReturn(member);

        // 1. Initial Revenue
        Map<String, Object> initialOverview = storeManagerService.getStoreOverview(1L);
        BigDecimal initialRevenue = (BigDecimal) initialOverview.get("todayRevenue");
        if (initialRevenue == null) initialRevenue = BigDecimal.ZERO;
        
        // 2. Perform Sale
        frontDeskService.processSale("M888", 100.0);
        
        // 3. Verify Revenue Increased
        Map<String, Object> newOverview = storeManagerService.getStoreOverview(1L);
        BigDecimal newRevenue = (BigDecimal) newOverview.get("todayRevenue");
        
        // 100.0 might be 100.00 in BigDecimal
        assertEquals(initialRevenue.add(BigDecimal.valueOf(100.0)).doubleValue(), newRevenue.doubleValue(), 0.01);
    }

    @Test
    public void testDeviceStatusLoop() {
        // 1. Setup Devices
        Device d1 = new Device(); d1.setSerialNumber("D1"); d1.setStatus("ONLINE");
        Device d2 = new Device(); d2.setSerialNumber("D2"); d2.setStatus("OFFLINE");
        deviceRepository.save(d1);
        deviceRepository.save(d2);
        
        // 2. Check Manager Overview
        Map<String, Object> overview = storeManagerService.getStoreOverview(1L);
        String rate = (String) overview.get("deviceOnlineRate");
        
        // Total 2, Offline 1 -> 50%
        // But there might be other devices in DB from data.sql?
        // Let's rely on relative change or just count logic
        long total = deviceRepository.count();
        long offline = deviceRepository.countByStatus("OFFLINE");
        double expected = total > 0 ? (double)(total - offline) / total * 100 : 0;
        
        assertEquals(String.format("%.1f", expected), rate);
    }
}
