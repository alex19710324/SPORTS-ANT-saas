package com.sportsant.saas.data.repository;

import com.sportsant.saas.data.entity.DataExportTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataExportTaskRepository extends JpaRepository<DataExportTask, Long> {
    List<DataExportTask> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
}
