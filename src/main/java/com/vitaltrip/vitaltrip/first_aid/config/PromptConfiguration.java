package com.vitaltrip.vitaltrip.first_aid.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Map;

@Slf4j
@Data
@Configuration
@ConfigurationPropertiesScan
public class PromptConfiguration {

    private String basePrompt;
    private Map<String, SymptomPrompt> symptomSpecific;

    @PostConstruct
    public void loadPrompts() {
        try {
            ClassPathResource resource = new ClassPathResource("prompt/first-aid.yml");
            try (InputStream inputStream = resource.getInputStream()) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(inputStream);

                @SuppressWarnings("unchecked")
                Map<String, Object> firstAidConfig = (Map<String, Object>) data.get("first-aid");

                this.basePrompt = (String) firstAidConfig.get("base-prompt");

                @SuppressWarnings("unchecked")
                Map<String, Map<String, String>> symptomData =
                    (Map<String, Map<String, String>>) firstAidConfig.get("symptom-specific");

                this.symptomSpecific = symptomData.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            SymptomPrompt symptom = new SymptomPrompt();
                            symptom.setSpecificPrompt(entry.getValue().get("specific-prompt"));
                            return symptom;
                        }
                    ));

                log.info("프롬프트 설정 로드 완료: {} 증상 유형", symptomSpecific.size());
            }
        } catch (Exception e) {
            log.error("프롬프트 설정 로드 실패", e);
            throw new RuntimeException("프롬프트 설정 초기화 실패", e);
        }
    }

    @Data
    public static class SymptomPrompt {
        private String specificPrompt;
    }
}
