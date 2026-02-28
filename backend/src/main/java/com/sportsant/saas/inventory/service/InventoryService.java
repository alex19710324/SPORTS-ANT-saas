package com.sportsant.saas.inventory.service;

import com.sportsant.saas.ai.event.SystemEvent;
import com.sportsant.saas.inventory.entity.InventoryItem;
import com.sportsant.saas.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public List<InventoryItem> getStoreInventory(Long storeId) {
        return inventoryRepository.findByStoreId(storeId);
    }

    public InventoryItem updateStock(Long storeId, String sku, int quantityChange) {
        InventoryItem item = inventoryRepository.findBySkuAndStoreId(sku, storeId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        int newQuantity = item.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }

        item.setQuantity(newQuantity);
        item.setLastUpdated(LocalDateTime.now());
        InventoryItem saved = inventoryRepository.save(item);

        // Check for Low Stock
        if (saved.getQuantity() <= saved.getThreshold()) {
            eventPublisher.publishEvent(new SystemEvent("INVENTORY_SERVICE", "LOW_STOCK_ALERT", Map.of(
                "sku", saved.getSku(),
                "name", saved.getName(),
                "current", saved.getQuantity()
            )));
        }

        return saved;
    }

    public List<InventoryItem> getLowStockItems(Long storeId) {
        List<InventoryItem> all = getStoreInventory(storeId);
        return all.stream()
                .filter(item -> item.getQuantity() <= item.getThreshold())
                .toList();
    }

    public InventoryItem addItem(InventoryItem item) {
        item.setLastUpdated(LocalDateTime.now());
        return inventoryRepository.save(item);
    }
}
