package com.sportsant.saas.workbench.controller;

import com.sportsant.saas.workbench.service.StoreManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workbench/manager")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StoreManagerController {

    @Autowired
    private StoreManagerService storeManagerService;

    @GetMapping("/overview")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('ADMIN')")
    public Map<String, Object> getOverview() {
        // In a real app, we would get the storeId from the authenticated user's context
        Long mockStoreId = 1L; 
        return storeManagerService.getStoreOverview(mockStoreId);
    }
}
