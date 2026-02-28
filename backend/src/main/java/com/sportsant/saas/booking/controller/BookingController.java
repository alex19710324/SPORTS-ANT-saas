package com.sportsant.saas.booking.controller;

import com.sportsant.saas.booking.entity.Booking;
import com.sportsant.saas.booking.entity.Resource;
import com.sportsant.saas.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/resources")
    public List<Resource> getResources() {
        return bookingService.getAvailableResources();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public List<Booking> getMyBookings(@RequestParam Long memberId) {
        return bookingService.getMemberBookings(memberId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public Booking createBooking(@RequestBody Map<String, Object> payload) {
        Long memberId = Long.valueOf(payload.get("memberId").toString());
        Long resourceId = Long.valueOf(payload.get("resourceId").toString());
        String start = payload.get("startTime").toString(); // ISO String
        String end = payload.get("endTime").toString();
        
        return bookingService.createBooking(
            memberId, 
            resourceId, 
            LocalDateTime.parse(start), 
            LocalDateTime.parse(end)
        );
    }

    @PostMapping("/validate-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FRONT_DESK') or hasRole('SECURITY')")
    public Map<String, Object> validateAccess(@RequestBody Map<String, String> payload) {
        String code = payload.get("accessCode");
        Booking booking = bookingService.validateEntry(code);
        return Map.of("status", "VALID", "message", "Access Granted", "booking", booking);
    }
}
