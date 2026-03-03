package com.sportsant.saas.workflow.repository;

import com.sportsant.saas.workflow.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByAssigneeId(String assigneeId);
    List<WorkOrder> findByStatusNot(String status);
    List<WorkOrder> findByType(String type);
    List<WorkOrder> findBySourceTypeAndSourceIdAndStatusIn(String sourceType, String sourceId, List<String> statuses);
}
