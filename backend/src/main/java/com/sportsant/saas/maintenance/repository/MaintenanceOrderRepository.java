package com.sportsant.saas.maintenance.repository;

import com.sportsant.saas.maintenance.entity.MaintenanceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceOrderRepository extends JpaRepository<MaintenanceOrder, Long> {
    List<MaintenanceOrder> findByStatus(String status);
    List<MaintenanceOrder> findByDeviceId(Long deviceId);
    List<MaintenanceOrder> findByAssignedTo(String assignedTo);
}
