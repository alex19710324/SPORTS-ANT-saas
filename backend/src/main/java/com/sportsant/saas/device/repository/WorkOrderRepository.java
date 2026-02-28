package com.sportsant.saas.device.repository;

import com.sportsant.saas.device.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByAssignedToAndStatus(Long technicianId, String status);
    List<WorkOrder> findByStatus(String status);
}
