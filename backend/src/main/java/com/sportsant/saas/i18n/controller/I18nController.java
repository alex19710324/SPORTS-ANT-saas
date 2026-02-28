package com.sportsant.saas.i18n.controller;

import com.sportsant.saas.i18n.entity.I18nKey;
import com.sportsant.saas.i18n.service.TranslationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/i18n")
@CrossOrigin(origins = "*", maxAge = 3600)
public class I18nController {

    @Autowired
    private TranslationService translationService;

    // --- P0: Region Detection ---
    @GetMapping("/detect")
    public Map<String, String> detectRegion(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();
        return translationService.detectRegion(ip);
    }

    // --- P0: Language Resource API ---
    @GetMapping("/pack/{lang}")
    public Map<String, String> getLanguagePack(@PathVariable String lang) {
        return translationService.getLanguagePack(lang);
    }

    // --- P1: Translation Management ---
    @PostMapping("/keys")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TRANSLATOR')")
    public I18nKey createKey(@RequestBody I18nKey key) {
        return translationService.createOrUpdateKey(key);
    }

    @PostMapping("/keys/{id}/auto-translate")
    @PreAuthorize("hasRole('ADMIN')")
    public void autoTranslate(@PathVariable Long id) {
        translationService.autoTranslate(id);
    }
}
