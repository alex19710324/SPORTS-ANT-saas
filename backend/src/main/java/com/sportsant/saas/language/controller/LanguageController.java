package com.sportsant.saas.language.controller;

import com.sportsant.saas.language.entity.LanguagePackage;
import com.sportsant.saas.language.service.LanguageService;
import com.sportsant.saas.language.service.RegionDetectionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/language")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private RegionDetectionService regionDetectionService;

    @GetMapping("/detect")
    public ResponseEntity<Map<String, String>> detectLanguage(HttpServletRequest request) {
        Locale locale = regionDetectionService.detectRegion(request);
        Map<String, String> response = new HashMap<>();
        response.put("lang", locale.toLanguageTag());
        response.put("country", locale.getCountry());
        response.put("display", locale.getDisplayName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public List<LanguagePackage> getAllLanguages() {
        return languageService.getAllLanguages();
    }

    @GetMapping("/{code}")
    public ResponseEntity<LanguagePackage> getLanguage(@PathVariable String code) {
        Optional<LanguagePackage> language = languageService.getLanguageByCode(code);
        return language.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LanguagePackage> createOrUpdateLanguage(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        String name = payload.get("name");
        String content = payload.get("content");
        String version = payload.get("version");
        
        if (code == null || name == null || content == null || version == null) {
            return ResponseEntity.badRequest().build();
        }

        LanguagePackage savedLanguage = languageService.createOrUpdateLanguage(code, name, content, version);
        return ResponseEntity.ok(savedLanguage);
    }
}
