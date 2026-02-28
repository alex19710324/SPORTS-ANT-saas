package com.sportsant.saas.ai.service;

import com.sportsant.saas.ai.entity.AiFeature;
import com.sportsant.saas.ai.repository.AiFeatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeatureStoreService {

    @Autowired
    private AiFeatureRepository featureRepository;

    /**
     * Mocks fetching feature values for a given entity (e.g. userId).
     * In a real system, this would query Redis (online) or Hive/Hudi (offline).
     */
    public Map<String, Object> getOnlineFeatures(Long entityId, List<String> featureNames) {
        // Validate features exist
        List<AiFeature> features = featureRepository.findAll();
        Map<String, AiFeature> featureMap = features.stream()
                .collect(Collectors.toMap(AiFeature::getName, f -> f));

        return featureNames.stream()
                .filter(featureMap::containsKey)
                .collect(Collectors.toMap(
                        name -> name,
                        name -> mockFeatureValue(name, entityId)
                ));
    }

    private Object mockFeatureValue(String featureName, Long entityId) {
        // Mock logic based on feature name suffix or type
        if (featureName.endsWith("_count")) {
            return (int) (Math.random() * 100);
        } else if (featureName.endsWith("_amount")) {
            return Math.random() * 1000;
        } else if (featureName.endsWith("_score")) {
            return Math.random();
        }
        return "mock_value";
    }

    public AiFeature registerFeature(String name, String type, String description, String source) {
        AiFeature feature = new AiFeature();
        feature.setName(name);
        feature.setType(type);
        feature.setDescription(description);
        feature.setSource(source);
        return featureRepository.save(feature);
    }

    public List<AiFeature> listFeatures() {
        return featureRepository.findAll();
    }
}
