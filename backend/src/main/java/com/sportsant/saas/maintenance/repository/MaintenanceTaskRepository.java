package com.sportsant.saas.maintenance.repository;

import com.sportsant.saas.maintenance.entity.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {
    List<MaintenanceTask> findByStatusNot(String status); // Not COMPLETED
}
