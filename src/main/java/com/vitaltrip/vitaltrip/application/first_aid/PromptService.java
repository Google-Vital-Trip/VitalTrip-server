package com.vitaltrip.vitaltrip.application.first_aid;

import com.vitaltrip.vitaltrip.infra.config.PromptConfiguration;
import com.vitaltrip.vitaltrip.domain.first_aid.EmergencySymptomType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromptService {

    private final PromptConfiguration promptConfig;

    public String buildPrompt(EmergencySymptomType symptomType, String userMessage) {
        String basePrompt = promptConfig.getBasePrompt()
            .replace("{symptomType}", symptomType.getDescription())
            .replace("{userMessage}", userMessage);

        String specificPrompt = getSymptomSpecificPrompt(symptomType);
        if (specificPrompt != null && !specificPrompt.trim().isEmpty()) {
            basePrompt += "\n\nSpecific guidance for " + symptomType.getDescription() + ":\n" + specificPrompt;
        }

        return basePrompt;
    }

    private String getSymptomSpecificPrompt(EmergencySymptomType symptomType) {
        if (promptConfig.getSymptomSpecific() == null) {
            return null;
        }

        PromptConfiguration.SymptomPrompt symptomPrompt =
            promptConfig.getSymptomSpecific().get(symptomType.name());

        return symptomPrompt.getSpecificPrompt();
    }
}
