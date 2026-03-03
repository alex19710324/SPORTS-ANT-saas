package com.sportsant.saas.store.controller;

import com.sportsant.saas.store.entity.Store;
import com.sportsant.saas.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "门店管理", description = "门店列表、状态监控、区域设置")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get all stores", description = "获取所有门店列表")
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @GetMapping("/dashboard/global")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ_MANAGER')")
    @Operation(summary = "Global store dashboard", description = "全球门店聚合看板")
    public Map<String, Object> getGlobalDashboard() {
        return storeService.getGlobalAggregates();
    }
}
