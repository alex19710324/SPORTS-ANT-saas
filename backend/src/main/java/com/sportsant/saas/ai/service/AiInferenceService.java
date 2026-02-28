package com.sportsant.saas.ai.service;

import com.sportsant.saas.ai.entity.AiModel;
import com.sportsant.saas.ai.repository.AiModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AiInferenceService {
    private static final Logger logger = LoggerFactory.getLogger(AiInferenceService.class);

    @Autowired
    private AiModelRepository aiModelRepository;

    /**
     * Mocks an inference call. In production, this would make an HTTP request to the Python/TensorFlow serving layer.
     */
    public Object predict(String modelName, Map<String, Object> inputData) {
        Optional<AiModel> modelOpt = aiModelRepository.findByName(modelName);
        
        if (modelOpt.isEmpty()) {
            throw new RuntimeException("Model not found: " + modelName);
        }

        AiModel model = modelOpt.get();
        if (!model.isActive()) {
            throw new RuntimeException("Model is not active: " + modelName);
        }

        logger.info("Invoking AI Model: {} (v{}) with input: {}", model.getName(), model.getVersion(), inputData);

        // MOCK LOGIC based on model type
        if (model.getType().equals("CLASSIFICATION")) {
            // e.g. Churn prediction
            return Map.of("prediction", "HIGH_RISK", "confidence", 0.85);
        } else if (model.getType().equals("GENERATION")) {
            // e.g. Marketing copy
            return Map.of("generated_text", "Experience the thrill of sports like never before! #SportsAnt");
        }

        return Map.of("status", "success", "message", "Mock prediction complete");
    }

    public AiModel registerModel(String name, String type, String version, String endpoint) {
        AiModel model = new AiModel();
        model.setName(name);
        model.setType(type);
        model.setVersion(version);
        model.setEndpointUrl(endpoint);
        model.setActive(true);
        return aiModelRepository.save(model);
    }
    
    public List<AiModel> listModels() {
        return aiModelRepository.findAll();
    }
}
