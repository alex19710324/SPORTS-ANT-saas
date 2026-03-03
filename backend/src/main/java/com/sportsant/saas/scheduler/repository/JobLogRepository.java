package com.sportsant.saas.scheduler.repository;

import com.sportsant.saas.scheduler.entity.JobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobLogRepository extends JpaRepository<JobLog, Long> {
    List<JobLog> findByJobIdOrderByStartTimeDesc(Long jobId);
}
