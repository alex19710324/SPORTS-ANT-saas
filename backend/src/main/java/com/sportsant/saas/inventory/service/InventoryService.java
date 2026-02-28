package com.sportsant.saas.inventory.service;

import com.sportsant.saas.communication.service.CommunicationService;
import com.sportsant.saas.inventory.entity.InventoryItem;
import com.sportsant.saas.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CommunicationService communicationService;

    public List<InventoryItem> getAllItems() {
        return inventoryRepository.findAll();
    }

    @Transactional
    public InventoryItem adjustStock(String sku, int quantityChange, String reason) {
        InventoryItem item = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Item not found: " + sku));

        int newQty = item.getQuantity() + quantityChange;
        if (newQty < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        item.setQuantity(newQty);

        // Low Stock Alert
        if (newQty <= item.getReorderPoint()) {
            communicationService.broadcast(
                "LOW STOCK ALERT: " + item.getName(), 
                "Current Qty: " + newQty + ". Reorder Point: " + item.getReorderPoint()
            );
        }

        return inventoryRepository.save(item);
    }

    @Transactional
    public InventoryItem createItem(InventoryItem item) {
        return inventoryRepository.save(item);
    }

    @Transactional
    public void updateStock(Long storeId, String sku, int quantityChange) {
        // StoreId ignored for MVP as items are global or we assume single store context
        adjustStock(sku, quantityChange, "WORK_ORDER_USAGE");
    }
}
