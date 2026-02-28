package com.sportsant.saas.repository;

import com.sportsant.saas.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
  Optional<SystemConfig> findByConfigKey(String configKey);
}
