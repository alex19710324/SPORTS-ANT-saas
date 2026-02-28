package com.sportsant.saas.ai.controller;

import com.sportsant.saas.ai.entity.AiFeature;
import com.sportsant.saas.ai.entity.AiModel;
import com.sportsant.saas.ai.service.AiInferenceService;
import com.sportsant.saas.ai.service.ContentGenerationService;
import com.sportsant.saas.ai.service.FeatureStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AiController {

    @Autowired
    private AiInferenceService aiInferenceService;

    @Autowired
    private FeatureStoreService featureStoreService;

    @Autowired
    private ContentGenerationService contentGenerationService;

    // --- Models ---
    @GetMapping("/models")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public List<AiModel> getAvailableModels() {
        return aiInferenceService.listModels();
    }

    @PostMapping("/models")
    @PreAuthorize("hasRole('ADMIN')")
    public AiModel registerModel(@RequestBody Map<String, String> payload) {
        return aiInferenceService.registerModel(
                payload.get("name"),
                payload.get("type"),
                payload.get("version"),
                payload.get("endpointUrl")
        );
    }

    // --- Features ---
    @GetMapping("/features")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public List<AiFeature> listFeatures() {
        return featureStoreService.listFeatures();
    }

    @PostMapping("/features")
    @PreAuthorize("hasRole('ADMIN')")
    public AiFeature registerFeature(@RequestBody Map<String, String> payload) {
        return featureStoreService.registerFeature(
                payload.get("name"),
                payload.get("type"),
                payload.get("description"),
                payload.get("source")
        );
    }

    // --- Inference & Generation ---
    @PostMapping("/predict/{modelName}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> predict(@PathVariable String modelName, @RequestBody Map<String, Object> inputData) {
        try {
            Object result = aiInferenceService.predict(modelName, inputData);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('MARKETING') or hasRole('ADMIN')")
    public ResponseEntity<Object> generateContent(@RequestBody Map<String, Object> payload) {
        try {
            String type = (String) payload.get("type");
            @SuppressWarnings("unchecked")
            Map<String, Object> params = (Map<String, Object>) payload.get("params");
            return ResponseEntity.ok(contentGenerationService.generateContent(type, params));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
