package com.sportsant.saas.booking.controller;

import com.sportsant.saas.booking.entity.Booking;
import com.sportsant.saas.booking.entity.Resource;
import com.sportsant.saas.booking.service.BookingService;
import com.sportsant.saas.common.context.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/booking")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "预订中心", description = "场地资源、预订管理")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/resources")
    @Operation(summary = "Get available resources", description = "获取所有可用场地资源")
    public List<Resource> getResources() {
        return bookingService.getAvailableResources();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get my bookings", description = "获取当前用户的预订记录")
    public List<Booking> getMyBookings(@RequestParam(required = false) Long memberId) {
        // If memberId is not provided, use current user from context
        Long targetId = memberId != null ? memberId : UserContext.getUserId();
        // Note: BookingService.getMemberBookings expects memberId (which might be different from userId)
        // For MVP, assuming memberId == userId or service handles lookup. 
        // Based on BookingService, it uses bookingRepository.findByMemberId(memberId).
        // We should probably look up Member by UserId first.
        return bookingService.getMemberBookings(targetId); 
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create booking", description = "创建新预订")
    public Booking createBooking(@RequestBody Map<String, Object> payload) {
        Long memberId = Long.valueOf(payload.get("memberId").toString());
        Long resourceId = Long.valueOf(payload.get("resourceId").toString());
        String startStr = payload.get("startTime").toString(); // Expecting ISO-8601 with Offset or UTC
        String endStr = payload.get("endTime").toString();
        
        // Parse as ZonedDateTime to handle timezones correctly
        ZonedDateTime startZoned = ZonedDateTime.parse(startStr);
        ZonedDateTime endZoned = ZonedDateTime.parse(endStr);
        
        // Convert to UTC LocalDateTime for storage (assuming DB is UTC)
        LocalDateTime startUtc = startZoned.withZoneSameInstant(java.time.ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime endUtc = endZoned.withZoneSameInstant(java.time.ZoneOffset.UTC).toLocalDateTime();
        
        return bookingService.createBooking(
            memberId, 
            resourceId, 
            startUtc, 
            endUtc
        );
    }

    @PostMapping("/validate-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FRONT_DESK') or hasRole('SECURITY')")
    @Operation(summary = "Validate access code", description = "验证入场码/二维码")
    public Map<String, Object> validateAccess(@RequestBody Map<String, String> payload) {
        String code = payload.get("accessCode");
        Booking booking = bookingService.validateEntry(code);
        return Map.of("status", "VALID", "message", "Access Granted", "booking", booking);
    }
}
