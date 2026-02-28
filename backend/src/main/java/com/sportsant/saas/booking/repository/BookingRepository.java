package com.sportsant.saas.booking.repository;

import com.sportsant.saas.booking.entity.Booking;
import com.sportsant.saas.booking.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByMemberId(Long memberId);
    List<Booking> findByResourceAndStartTimeBetween(Resource resource, LocalDateTime start, LocalDateTime end);
    Optional<Booking> findByAccessCode(String accessCode);
}
