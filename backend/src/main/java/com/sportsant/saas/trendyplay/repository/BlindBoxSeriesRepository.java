package com.sportsant.saas.trendyplay.repository;

import com.sportsant.saas.trendyplay.entity.BlindBoxSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlindBoxSeriesRepository extends JpaRepository<BlindBoxSeries, String> {
    List<BlindBoxSeries> findByTenantIdAndStatus(String tenantId, String status);
    List<BlindBoxSeries> findByIsGlobalLimitedTrue();
}
