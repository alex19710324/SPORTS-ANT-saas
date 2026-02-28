package com.sportsant.saas.inventory.service;

import com.sportsant.saas.ai.event.SystemEvent;
import com.sportsant.saas.inventory.entity.InventoryItem;
import com.sportsant.saas.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateStock_Normal() {
        Long storeId = 1L;
        String sku = "SKU123";
        InventoryItem item = new InventoryItem();
        item.setSku(sku);
        item.setQuantity(10);
        item.setThreshold(5);
        item.setName("Test Item");

        when(inventoryRepository.findBySkuAndStoreId(sku, storeId)).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(i -> i.getArgument(0));

        InventoryItem result = inventoryService.updateStock(storeId, sku, -2);
        
        assertEquals(8, result.getQuantity());
        verify(eventPublisher, never()).publishEvent(any(SystemEvent.class));
    }

    @Test
    public void testUpdateStock_LowStockAlert() {
        Long storeId = 1L;
        String sku = "SKU123";
        InventoryItem item = new InventoryItem();
        item.setSku(sku);
        item.setQuantity(6);
        item.setThreshold(5);
        item.setName("Test Item");

        when(inventoryRepository.findBySkuAndStoreId(sku, storeId)).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(i -> i.getArgument(0));

        // Deduct 2, new quantity = 4 (below threshold 5)
        InventoryItem result = inventoryService.updateStock(storeId, sku, -2);
        
        assertEquals(4, result.getQuantity());
        verify(eventPublisher, times(1)).publishEvent(any(SystemEvent.class));
    }

    @Test
    public void testUpdateStock_Insufficient() {
        Long storeId = 1L;
        String sku = "SKU123";
        InventoryItem item = new InventoryItem();
        item.setQuantity(1);

        when(inventoryRepository.findBySkuAndStoreId(sku, storeId)).thenReturn(Optional.of(item));

        assertThrows(RuntimeException.class, () -> inventoryService.updateStock(storeId, sku, -2));
    }
}
