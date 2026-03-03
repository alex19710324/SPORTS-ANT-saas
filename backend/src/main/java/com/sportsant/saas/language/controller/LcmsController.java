package com.sportsant.saas.language.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.language.entity.LanguagePackage;
import com.sportsant.saas.language.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lcms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LcmsController {

    @Autowired
    private LanguageService languageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, String>>> getProjects() {
        Map<String, String> project = new HashMap<>();
        project.put("code", "saas-core");
        project.put("name", "SPORTS ANT SaaS Core");
        return ResponseEntity.ok(Collections.singletonList(project));
    }

    @GetMapping("/projects/{projectCode}/keys")
    public ResponseEntity<List<Map<String, Object>>> getKeys(@PathVariable String projectCode) {
        Optional<LanguagePackage> pkg = languageService.getLanguageByCode("zh-CN");
        if (pkg.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        try {
            Map<String, Object> content = objectMapper.readValue(pkg.get().getContent(), new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> keys = new ArrayList<>();
            flattenKeys(content, "", keys);
            return ResponseEntity.ok(keys);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/bundle/{projectCode}/{locale}")
    public ResponseEntity<Map<String, Object>> getBundle(@PathVariable String projectCode, @PathVariable String locale) {
        Optional<LanguagePackage> pkg = languageService.getLanguageByCode(locale);
        if (pkg.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyMap());
        }
        try {
            Map<String, Object> content = objectMapper.readValue(pkg.get().getContent(), new TypeReference<Map<String, Object>>() {});
            Map<String, Object> flatContent = new HashMap<>();
            flattenMap(content, "", flatContent);
            return ResponseEntity.ok(flatContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/keys")
    public ResponseEntity<?> createKey(@RequestBody Map<String, String> payload) {
        String keyName = payload.get("keyName");
        // String description = payload.get("description"); // Not used in simple storage
        
        List<LanguagePackage> allPackages = languageService.getAllLanguages();
        for (LanguagePackage pkg : allPackages) {
            try {
                Map<String, Object> content = objectMapper.readValue(pkg.getContent(), new TypeReference<Map<String, Object>>() {});
                addNestedKey(content, keyName, ""); 
                
                String newContent = objectMapper.writeValueAsString(content);
                languageService.createOrUpdateLanguage(pkg.getCode(), pkg.getName(), newContent, incrementVersion(pkg.getVersion()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/translations")
    public ResponseEntity<?> updateTranslation(@RequestParam String keyId, @RequestParam String locale, @RequestBody Map<String, String> payload) {
        String value = payload.get("value");
        // keyId is treated as keyName here because we don't have separate Key IDs
        String keyName = keyId; 

        Optional<LanguagePackage> pkgOpt = languageService.getLanguageByCode(locale);
        if (pkgOpt.isPresent()) {
            LanguagePackage pkg = pkgOpt.get();
            try {
                Map<String, Object> content = objectMapper.readValue(pkg.getContent(), new TypeReference<Map<String, Object>>() {});
                addNestedKey(content, keyName, value);
                
                String newContent = objectMapper.writeValueAsString(content);
                languageService.createOrUpdateLanguage(pkg.getCode(), pkg.getName(), newContent, incrementVersion(pkg.getVersion()));
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    private void flattenKeys(Map<String, Object> map, String prefix, List<Map<String, Object>> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flattenKeys((Map<String, Object>) entry.getValue(), key, result);
            } else {
                Map<String, Object> item = new HashMap<>();
                item.put("id", key);
                item.put("keyName", key);
                item.put("description", "Auto-extracted");
                result.add(item);
            }
        }
    }
    
    private void flattenMap(Map<String, Object> map, String prefix, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flattenMap((Map<String, Object>) entry.getValue(), key, result);
            } else {
                result.put(key, entry.getValue());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addNestedKey(Map<String, Object> map, String key, String value) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(parts[i], k -> new HashMap<>());
        }
        current.put(parts[parts.length - 1], value);
    }
    
    private String incrementVersion(String version) {
        if (version == null) return "1.0";
        // Simple append for demo, real versioning would parse semver
        return version + "-r" + System.currentTimeMillis();
    }
}
