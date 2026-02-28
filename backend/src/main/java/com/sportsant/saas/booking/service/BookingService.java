package com.sportsant.saas.booking.service;

import com.sportsant.saas.booking.entity.Booking;
import com.sportsant.saas.booking.entity.Resource;
import com.sportsant.saas.booking.repository.BookingRepository;
import com.sportsant.saas.booking.repository.ResourceRepository;
import com.sportsant.saas.data.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private AnalyticsService analyticsService;

    public List<Resource> getAvailableResources() {
        return resourceRepository.findByActiveTrue();
    }

    public List<Booking> getMemberBookings(Long memberId) {
        return bookingRepository.findByMemberId(memberId);
    }

    @Transactional
    public Booking createBooking(Long memberId, Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        // 1. Conflict Check
        // Using strict time overlap logic
        // findByResourceAndStartTimeLessThanAndEndTimeGreaterThan
        // For MVP, relying on simple query. Let's assume start time matches slots.
        List<Booking> conflicts = bookingRepository.findByResourceAndStartTimeBetween(resource, startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time slot already booked");
        }

        // 2. Dynamic Pricing (Simple MVP: If peak hour, +20%)
        long hours = java.time.Duration.between(startTime, endTime).toHours();
        if (hours == 0) hours = 1; // Min 1 hour
        double price = resource.getHourlyRate() * hours;
        
        Map<String, Object> data = analyticsService.getDashboardData();
        Map<String, Integer> peakHours = (Map<String, Integer>) data.get("peakHours");
        
        String hourKey = String.format("%02d:00", startTime.getHour());
        // Safe check for null
        if (peakHours != null && peakHours.getOrDefault(hourKey, 0) > 60) {
            price *= 1.2; // Peak surcharge
        }

        Booking booking = new Booking();
        booking.setMemberId(memberId);
        booking.setResource(resource);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setTotalPrice(price);
        booking.setAccessCode(UUID.randomUUID().toString()); // Generate QR content
        
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking validateEntry(String accessCode) {
        Booking booking = bookingRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new RuntimeException("Invalid Access Code"));

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is " + booking.getStatus());
        }

        // Check time window (allow entry 15 mins before)
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(booking.getStartTime().minusMinutes(15)) || now.isAfter(booking.getEndTime())) {
            throw new RuntimeException("Outside booking window");
        }

        booking.setStatus("COMPLETED"); // Mark as used/entered
        return bookingRepository.save(booking);
    }
}
