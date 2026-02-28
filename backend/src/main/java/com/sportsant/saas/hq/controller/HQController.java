package com.sportsant.saas.hq.controller;

import com.sportsant.saas.hq.service.HQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hq")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HQController {

    @Autowired
    private HQService hqService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ')")
    public Map<String, Object> getDashboard() {
        return hqService.getExecutiveDashboard();
    }
}
