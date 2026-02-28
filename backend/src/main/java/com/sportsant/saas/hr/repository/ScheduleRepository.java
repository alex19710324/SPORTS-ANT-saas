package com.sportsant.saas.hr.repository;

import com.sportsant.saas.hr.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByStoreIdAndStartTimeBetween(Long storeId, LocalDateTime start, LocalDateTime end);
}
