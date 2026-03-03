package com.sportsant.saas.trendyplay.service;

import com.sportsant.saas.trendyplay.entity.BlindBoxSeries;
import com.sportsant.saas.trendyplay.entity.UserCollectible;
import com.sportsant.saas.trendyplay.repository.BlindBoxSeriesRepository;
import com.sportsant.saas.trendyplay.repository.UserCollectibleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class TrendyPlayService {
    
    private final BlindBoxSeriesRepository seriesRepository;
    private final UserCollectibleRepository collectibleRepository;
    private final Random random = new Random();

    public TrendyPlayService(BlindBoxSeriesRepository seriesRepository, 
                             UserCollectibleRepository collectibleRepository) {
        this.seriesRepository = seriesRepository;
        this.collectibleRepository = collectibleRepository;
    }

    public List<BlindBoxSeries> getActiveSeries(String tenantId) {
        return seriesRepository.findByTenantIdAndStatus(tenantId, "ACTIVE");
    }

    public List<BlindBoxSeries> getGlobalLimitedSeries() {
        return seriesRepository.findByIsGlobalLimitedTrue();
    }

    @Transactional
    public UserCollectible purchaseBlindBox(String userId, String tenantId, String seriesId) {
        BlindBoxSeries series = seriesRepository.findById(seriesId)
            .orElseThrow(() -> new RuntimeException("Series not found"));
            
        if (series.getTotalStock() <= series.getSoldCount()) {
            throw new RuntimeException("Sold out!");
        }
        
        // Mock Payment Process
        // paymentService.charge(userId, series.getPrice(), series.getCurrency());
        
        // Update Stock
        series.setSoldCount(series.getSoldCount() + 1);
        if (series.getTotalStock().equals(series.getSoldCount())) {
            series.setStatus("SOLD_OUT");
        }
        seriesRepository.save(series);
        
        // Generate Collectible (Gacha Logic)
        UserCollectible collectible = generateRandomCollectible(userId, tenantId, series);
        return collectibleRepository.save(collectible);
    }
    
    public List<UserCollectible> getUserCollection(String userId) {
        return collectibleRepository.findByUserId(userId);
    }

    private UserCollectible generateRandomCollectible(String userId, String tenantId, BlindBoxSeries series) {
        UserCollectible item = new UserCollectible();
        item.setUserId(userId);
        item.setTenantId(tenantId);
        item.setBlindBoxSeriesId(series.getId());
        
        // Random Rarity Logic
        double roll = random.nextDouble();
        if (roll < 0.01) {
            item.setRarity("SECRET"); // 1%
            item.setCollectibleName(series.getName() + " - Hidden Edition (Gold)");
            item.setMarketValue(series.getPrice().multiply(new BigDecimal(50)));
        } else if (roll < 0.1) {
            item.setRarity("LEGENDARY"); // 9%
            item.setCollectibleName(series.getName() + " - Legendary");
            item.setMarketValue(series.getPrice().multiply(new BigDecimal(10)));
        } else if (roll < 0.3) {
            item.setRarity("RARE"); // 20%
            item.setCollectibleName(series.getName() + " - Rare");
            item.setMarketValue(series.getPrice().multiply(new BigDecimal(3)));
        } else {
            item.setRarity("COMMON"); // 70%
            item.setCollectibleName(series.getName() + " - Common");
            item.setMarketValue(series.getPrice());
        }
        
        item.setCurrency(series.getCurrency());
        item.setIsDigitalTwin(true); // Always digital in our SaaS
        item.setNftTokenId(UUID.randomUUID().toString());
        
        return item;
    }
}
