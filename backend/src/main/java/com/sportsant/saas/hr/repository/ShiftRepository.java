package com.sportsant.saas.hr.repository;

import com.sportsant.saas.hr.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
