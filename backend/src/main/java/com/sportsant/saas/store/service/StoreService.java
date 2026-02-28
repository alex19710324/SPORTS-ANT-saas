package com.sportsant.saas.store.service;

import com.sportsant.saas.store.entity.Store;
import com.sportsant.saas.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    public Map<String, Object> getGlobalAggregates() {
        List<Store> allStores = storeRepository.findAll();
        
        BigDecimal totalRevenue = allStores.stream()
                .map(Store::getTodayRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalVisitors = allStores.stream()
                .mapToInt(Store::getTodayVisitors)
                .sum();
        
        long activeStores = allStores.stream()
                .filter(s -> "NORMAL".equals(s.getStatus()))
                .count();
        
        long alertStores = allStores.stream()
                .filter(s -> "WARNING".equals(s.getStatus()))
                .count();

        Map<String, Object> aggregates = new HashMap<>();
        aggregates.put("totalRevenue", totalRevenue);
        aggregates.put("totalVisitors", totalVisitors);
        aggregates.put("activeStores", activeStores);
        aggregates.put("alertStores", alertStores);
        aggregates.put("totalStores", allStores.size());
        
        return aggregates;
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
}
