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
        String prompt = createEmergencyPrompt(request.emergencyType(), request.userMessage());
        String advice = geminiClient.generateContent(prompt);

        // todo - advice를 위한 프롬프트 고도화, confidence 계산식, 추천 블로그 선택 알고리즘
        return EmergencyChatAdviceResponse.from(advice, "temp", 100.0,
            List.of("https://www.eunwoo-levi.blog/"));
    }

    // 임시 프롬프트 - 차후 응급처치 메뉴얼 제공 알고리즘 수정 예정
    private String createEmergencyPrompt(String emergencyType, String userMessage) {
        return String.format("""
            당신은 응급의료 전문가입니다. 다음 응급상황에 대해 도움을 주세요:
            
            응급상황 유형: %s
            사용자 메시지: %s
            
            간결하고 명확하게 즉시 취해야 할 행동을 알려주세요.
            생명과 관련된 응급상황이므로 정확한 정보만 제공해주세요.
            """, emergencyType, userMessage);
    }

}
