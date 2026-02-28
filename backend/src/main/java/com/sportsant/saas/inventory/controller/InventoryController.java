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
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('TECHNICIAN') or hasRole('FRONT_DESK') or hasRole('ADMIN')")
    public List<InventoryItem> getInventory(@RequestParam Long storeId) {
        return inventoryService.getStoreInventory(storeId);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('ADMIN')")
    public InventoryItem addItem(@RequestBody InventoryItem item) {
        return inventoryService.addItem(item);
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasRole('STORE_MANAGER') or hasRole('TECHNICIAN') or hasRole('FRONT_DESK') or hasRole('ADMIN')")
    public InventoryItem adjustStock(@RequestBody Map<String, Object> payload) {
        Long storeId = Long.valueOf(payload.get("storeId").toString());
        String sku = (String) payload.get("sku");
        int quantity = (int) payload.get("quantity");
        return inventoryService.updateStock(storeId, sku, quantity);
    }
}
