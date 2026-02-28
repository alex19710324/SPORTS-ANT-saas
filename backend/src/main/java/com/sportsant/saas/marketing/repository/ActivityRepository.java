package com.sportsant.saas.marketing.repository;

import com.sportsant.saas.marketing.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByStatus(String status);
    List<Activity> findByType(String type);
}
