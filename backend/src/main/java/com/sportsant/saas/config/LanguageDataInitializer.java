package com.sportsant.saas.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.language.entity.LanguagePackage;
import com.sportsant.saas.language.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class LanguageDataInitializer implements CommandLineRunner {

    @Autowired
    private LanguageService languageService;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        Resource[] languageFiles = resourcePatternResolver.getResources("classpath*:i18n/*.json");
        
        if (languageFiles == null) {
            return;
        }

        for (Resource resource : languageFiles) {
            String filename = resource.getFilename();
            if (filename == null || !filename.endsWith(".json")) {
                continue;
            }

            String code = filename.replace(".json", "");
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                String content = FileCopyUtils.copyToString(reader);
                
                // Validate JSON
                try {
                    objectMapper.readTree(content);
                } catch (Exception e) {
                    System.err.println("Invalid JSON in language file: " + filename);
                    continue;
                }

                Optional<LanguagePackage> existing = languageService.getLanguageByCode(code);
                if (existing.isEmpty()) {
                    System.out.println("Initializing language package: " + code);
                    languageService.createOrUpdateLanguage(code, getLanguageName(code), content, "1.0");
                } else {
                    System.out.println("Language package exists: " + code);
                }
            } catch (Exception e) {
                System.err.println("Error reading language file: " + filename);
                e.printStackTrace();
            }
        }
    }

    private String getLanguageName(String code) {
        switch (code) {
            case "zh-CN": return "Chinese (Simplified)";
            case "en-US": return "English (US)";
            case "fr-FR": return "French";
            case "es-ES": return "Spanish";
            case "de-DE": return "German";
            case "it-IT": return "Italian";
            case "pt-PT": return "Portuguese";
            case "ar-SA": return "Arabic";
            default: return code;
        }
    }
}
