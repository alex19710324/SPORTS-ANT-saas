package com.sportsant.saas.data.repository;

import com.sportsant.saas.data.entity.DailyMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailyMetricRepository extends JpaRepository<DailyMetric, Long> {
    List<DailyMetric> findByMetricNameAndDateBetween(String metricName, LocalDateTime start, LocalDateTime end);
}
