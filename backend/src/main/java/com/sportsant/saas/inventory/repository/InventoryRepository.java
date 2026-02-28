package com.sportsant.saas.inventory.repository;

import com.sportsant.saas.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByStoreId(Long storeId);
    Optional<InventoryItem> findBySkuAndStoreId(String sku, Long storeId);
}
