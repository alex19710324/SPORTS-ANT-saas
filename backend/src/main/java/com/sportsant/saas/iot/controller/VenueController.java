package com.sportsant.saas.iot.controller;

import com.sportsant.saas.iot.entity.Zone;
import com.sportsant.saas.iot.service.SmartVenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/iot/venue")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VenueController {

    @Autowired
    private SmartVenueService smartVenueService;

    @GetMapping("/zones")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STORE_MANAGER') or hasRole('TECHNICIAN')")
    public List<Zone> getZones() {
        return smartVenueService.getAllZones();
    }
}
