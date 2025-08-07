package com.vitaltrip.vitaltrip.domain.first_aid.service;

import com.vitaltrip.vitaltrip.domain.ai.client.GeminiClient;
import com.vitaltrip.vitaltrip.domain.first_aid.dto.EmergencyChatAdviceRequest;
import com.vitaltrip.vitaltrip.domain.first_aid.dto.EmergencyChatAdviceResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirstAidService {

    private final GeminiClient geminiClient;

    public EmergencyChatAdviceResponse generateEmergencyAdvice(EmergencyChatAdviceRequest request) {
        String prompt = createEmergencyPrompt(request.symptomType(), request.symptomDetail());
        String advice = geminiClient.generateContent(prompt);

        // todo - advice를 위한 프롬프트 고도화, confidence 계산식, 추천 블로그 선택 알고리즘
        return EmergencyChatAdviceResponse.from(advice, "summary", "temp", 100.0,
            List.of("https://www.eunwoo-levi.blog/"));
    }

    // 임시 프롬프트 - 차후 응급처치 메뉴얼 제공 알고리즘 수정 예정
    private String createEmergencyPrompt(String emergencyType, String userMessage) {
        return String.format("""
            You are an emergency medical expert. Please provide assistance for the following emergency situation:
            
            Symptom Type: %s
            Symptom Detail: %s
            
            Please provide concise and clear instructions on what actions should be taken immediately.
            Since this is a life-related emergency situation, please provide only accurate information.
            """, emergencyType, userMessage);
    }

}
