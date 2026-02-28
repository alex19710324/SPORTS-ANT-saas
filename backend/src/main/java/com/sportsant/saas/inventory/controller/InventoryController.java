package com.sportsant.saas.inventory.controller;

import com.sportsant.saas.inventory.entity.InventoryItem;
import com.sportsant.saas.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER') or hasRole('TECHNICIAN')")
    public List<InventoryItem> getAllItems() {
        return inventoryService.getAllItems();
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER')")
    public InventoryItem adjustStock(@RequestBody Map<String, Object> payload) {
        String sku = (String) payload.get("sku");
        int change = (Integer) payload.get("quantityChange");
        String reason = (String) payload.get("reason");
        return inventoryService.adjustStock(sku, change, reason);
    }
    
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryItem createItem(@RequestBody InventoryItem item) {
        return inventoryService.createItem(item);
    }
}
