package com.sportsant.saas.inventory.service;

import com.sportsant.saas.inventory.entity.InventoryItem;
import com.sportsant.saas.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Map<String, Object>> getDemandForecast() {
        List<InventoryItem> items = inventoryRepository.findAll();
        List<Map<String, Object>> forecast = new ArrayList<>();
        Random rand = new Random();

        for (InventoryItem item : items) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("sku", item.getSku());
            entry.put("name", item.getName());
            entry.put("currentStock", item.getQuantity());
            
            // Mock AI Prediction
            // Predict demand for next 7 days
            int predictedDemand = rand.nextInt(20) + 5; 
            entry.put("predictedDemand", predictedDemand);
            
            // Suggest Reorder if stock < predicted
            boolean reorder = item.getQuantity() < predictedDemand;
            entry.put("reorderSuggested", reorder);
            if (reorder) {
                entry.put("suggestedReorderQty", predictedDemand - item.getQuantity() + 10); // Buffer
            } else {
                entry.put("suggestedReorderQty", 0);
            }
            
            forecast.add(entry);
        }
        return forecast;
    }

    public List<InventoryItem> getAllItems() {
        return inventoryRepository.findAll();
    }

    public InventoryItem getItem(String sku) {
        return inventoryRepository.findBySku(sku).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Transactional
    public InventoryItem adjustStock(String sku, int quantityChange, String reason) {
        InventoryItem item = getItem(sku);
        int newQty = item.getQuantity() + quantityChange;
        if (newQty < 0) {
            throw new RuntimeException("Insufficient stock for SKU: " + sku);
        }
        item.setQuantity(newQty);
        
        // Log transaction (Mock)
        System.out.println("Stock Adjust: " + sku + " change: " + quantityChange + " reason: " + reason);
        
        return inventoryRepository.save(item);
    }

    @Transactional
    public InventoryItem updateStock(Long itemId, String reason, int change) {
        InventoryItem item = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        
        int newQty = item.getQuantity() + change;
        if (newQty < 0) {
            throw new RuntimeException("Insufficient stock for item: " + item.getName());
        }
        item.setQuantity(newQty);
        
        System.out.println("Stock Update: " + item.getName() + " change: " + change + " reason: " + reason);
        return inventoryRepository.save(item);
    }

    @Transactional
    public InventoryItem createItem(InventoryItem item) {
        if (inventoryRepository.findBySku(item.getSku()).isPresent()) {
            throw new RuntimeException("SKU already exists");
        }
        return inventoryRepository.save(item);
    }
}
