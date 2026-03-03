package com.sportsant.saas.workflow.repository;

import com.sportsant.saas.workflow.entity.WorkOrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderLogRepository extends JpaRepository<WorkOrderLog, Long> {
    List<WorkOrderLog> findByWorkOrderIdOrderByCreatedAtDesc(Long workOrderId);
}
