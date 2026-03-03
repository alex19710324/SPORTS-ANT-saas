package com.sportsant.saas.ai.controller;

import com.sportsant.saas.ai.entity.AiSuggestion;
import com.sportsant.saas.ai.service.AiBrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiController {

    @Autowired
    private AiBrainService aiBrainService;

    @GetMapping("/suggestions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ') or hasRole('STORE_MANAGER')")
    public List<AiSuggestion> getSuggestions() {
        return aiBrainService.getPendingSuggestions();
    }

    @PostMapping("/suggestions/{id}/execute")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ') or hasRole('STORE_MANAGER')")
    public AiSuggestion executeSuggestion(@PathVariable Long id) {
        return aiBrainService.executeSuggestion(id);
    }

    @PostMapping("/suggestions/{id}/ignore")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HQ') or hasRole('STORE_MANAGER')")
    public AiSuggestion ignoreSuggestion(@PathVariable Long id) {
        return aiBrainService.ignoreSuggestion(id);
    }
}
