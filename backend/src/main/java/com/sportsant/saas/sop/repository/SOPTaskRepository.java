package com.sportsant.saas.sop.repository;

import com.sportsant.saas.sop.entity.SOPTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SOPTaskRepository extends JpaRepository<SOPTask, Long> {
    List<SOPTask> findByAssignedToAndStatus(String assignedTo, String status);
    List<SOPTask> findByStatus(String status);
}
