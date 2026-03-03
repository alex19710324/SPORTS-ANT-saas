package com.sportsant.saas.trendyplay.repository;

import com.sportsant.saas.trendyplay.entity.UserCollectible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCollectibleRepository extends JpaRepository<UserCollectible, String> {
    List<UserCollectible> findByUserIdAndTenantId(String userId, String tenantId);
    List<UserCollectible> findByUserId(String userId);
}
