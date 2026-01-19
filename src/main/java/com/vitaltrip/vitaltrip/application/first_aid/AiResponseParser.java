package com.vitaltrip.vitaltrip.application.first_aid;

import com.vitaltrip.vitaltrip.presentation.first_aid.dto.GeminiParsedResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiResponseParser {

    private static final Pattern CONTENT_PATTERN = Pattern.compile(
        "CONTENT:\\s*\\n(.*?)\\n\\nSUMMARY:", Pattern.DOTALL);
    private static final Pattern SUMMARY_PATTERN = Pattern.compile(
        "SUMMARY:\\s*\\n(.*?)\\n\\nRECOMMENDED_ACTION:", Pattern.DOTALL);
    private static final Pattern RECOMMENDED_ACTION_PATTERN = Pattern.compile(
        "RECOMMENDED_ACTION:\\s*\\n(.*?)\\n\\nDISCLAIMER:", Pattern.DOTALL);
    private static final Pattern DISCLAIMER_PATTERN = Pattern.compile("DISCLAIMER:\\s*\\n(.*?)$",
        Pattern.DOTALL);

    public GeminiParsedResponse parseResponse(String aiResponse) {
        try {
            return new GeminiParsedResponse(
                extractSection(aiResponse, CONTENT_PATTERN, "응급처치 절차를 따라주세요."),
                extractSection(aiResponse, SUMMARY_PATTERN, "응급상황에 대한 조치가 필요합니다."),
                extractSection(aiResponse, RECOMMENDED_ACTION_PATTERN, "즉시 응급실로 이동하세요."),
                extractSection(aiResponse, DISCLAIMER_PATTERN,
                    "이는 AI의 임시 응급처치 조언입니다. 참고만 하시고 빠른 시간 내에 전문의에게 상담받으세요.")
            );
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage());
            return getDefaultResponse();
        }
    }

    private String extractSection(String response, Pattern pattern, String fallback) {
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return fallback;
    }

    private GeminiParsedResponse getDefaultResponse() {
        return new GeminiParsedResponse(
            "Move to a safe place\nAssess the situation\nCall emergency services\nStay calm and follow basic first aid principles",
            "Emergency situation requires immediate attention",
            "Contact emergency services immediately",
            "This is temporary AI first aid advice. Please seek professional medical care immediately."
        );
    }

}
