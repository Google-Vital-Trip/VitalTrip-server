package com.vitaltrip.vitaltrip.first_aid.service;

import com.vitaltrip.vitaltrip.ai.client.GeminiClient;
import com.vitaltrip.vitaltrip.first_aid.domain.EmergencySymptomType;
import com.vitaltrip.vitaltrip.first_aid.dto.EmergencyChatAdviceRequest;
import com.vitaltrip.vitaltrip.first_aid.dto.EmergencyChatAdviceResponse;
import com.vitaltrip.vitaltrip.first_aid.dto.GeminiParsedResponse;
import com.vitaltrip.vitaltrip.location.client.BigDataCloudClient;
import com.vitaltrip.vitaltrip.location.dto.BigDataCloudResponse;
import com.vitaltrip.vitaltrip.location.dto.EmergencyContact;
import com.vitaltrip.vitaltrip.location.service.EmergencyContactService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirstAidService {

    private final GeminiClient geminiClient;
    private final PromptService promptService;
    private final AiResponseParser aiResponseParser;
    private final BigDataCloudClient bigDataCloudClient;
    private final EmergencyContactService emergencyContactService;

    public EmergencyChatAdviceResponse generateEmergencyAdvice(EmergencyChatAdviceRequest request) {

        BigDataCloudResponse locationResponse = bigDataCloudClient.reverseGeocode(
            request.latitude(), request.longitude());
        EmergencyContact emergencyContact = emergencyContactService.getEmergencyContact(
            locationResponse.countryCode());

        String prompt = promptService.buildPrompt(request.symptomType(), request.symptomDetail());
        String aiResponse = geminiClient.generateContent(prompt);

        GeminiParsedResponse parsedResponse = aiResponseParser.parseResponse(aiResponse);

        Integer confidence = calculateConfidence(parsedResponse.content());
        List<String> blogLinks = getBlogLink(request.symptomType());

        return EmergencyChatAdviceResponse.of(
            parsedResponse.content(),
            parsedResponse.summary(),
            parsedResponse.recommendedAction(),
            emergencyContact,
            parsedResponse.disclaimer(),
            confidence,
            blogLinks
        );
    }

    private Integer calculateConfidence(String content) {
        return 100;
    }

    private List<String> getBlogLink(EmergencySymptomType type) {
        return List.of("https://www.eunwoo-levi.blog/");
    }

}
