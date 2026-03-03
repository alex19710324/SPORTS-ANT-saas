package com.sportsant.saas.booking.repository;

import com.sportsant.saas.booking.entity.Booking;
import com.sportsant.saas.booking.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByMemberId(Long memberId);
    
    // Old: findByResourceAndStartTimeBetween (flawed conflict check)
    // New: Check for any overlap
    // (StartA < EndB) AND (EndA > StartB)
    @Query("SELECT b FROM Booking b WHERE b.resource = :resource AND b.startTime < :end AND b.endTime > :start AND b.status <> 'CANCELLED'")
    List<Booking> findConflicts(Resource resource, LocalDateTime start, LocalDateTime end);

    List<Booking> findByResourceAndStartTimeBetween(Resource resource, LocalDateTime start, LocalDateTime end);
    Optional<Booking> findByAccessCode(String accessCode);
}
