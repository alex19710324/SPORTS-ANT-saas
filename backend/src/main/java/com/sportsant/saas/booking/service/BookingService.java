package com.sportsant.saas.booking.service;

import com.sportsant.saas.booking.entity.Booking;
import com.sportsant.saas.booking.entity.Resource;
import com.sportsant.saas.booking.repository.BookingRepository;
import com.sportsant.saas.booking.repository.ResourceRepository;
import com.sportsant.saas.data.service.AnalyticsService;
import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.membership.service.MembershipService;
import com.sportsant.saas.hr.service.WorkforceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private WorkforceService workforceService;

    public List<Resource> getAvailableResources() {
        return resourceRepository.findByActiveTrue();
    }

    public List<Booking> getMemberBookings(Long memberId) {
        return bookingRepository.findByMemberId(memberId);
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public Booking createBooking(Long memberId, Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        // 1. Conflict Check (Enhanced)
        List<Booking> conflicts = bookingRepository.findConflicts(resource, startTime, endTime);
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
        
        // 3. Payment Processing (Deduct Balance)
        try {
            membershipService.deductBalance(memberId, price);
        } catch (Exception e) {
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }

        Booking savedBooking = bookingRepository.save(booking);

        // 4. Financial Record (Revenue Recognition)
        // Debit: Advance from Customers, Credit: Service Revenue
        financeService.createVoucher(
            "POS_SALE_BALANCE", 
            savedBooking.getId(), 
            BigDecimal.valueOf(price), 
            "Booking: " + resource.getName(), 
            "CN"
        );

        // 5. HR Auto-Scheduling (If resource requires staff, e.g., Personal Training)
        if ("PERSONAL_TRAINER".equals(resource.getType())) {
            workforceService.autoAssignTrainer(savedBooking);
        }

        return savedBooking;
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
