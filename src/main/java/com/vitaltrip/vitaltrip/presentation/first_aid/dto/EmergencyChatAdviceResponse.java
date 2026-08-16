package com.vitaltrip.vitaltrip.presentation.first_aid.dto;

import com.vitaltrip.vitaltrip.presentation.location.dto.CountryIdentificationResponse;

import java.util.List;

public record EmergencyChatAdviceResponse(
        String content,
        String summary,
        String recommendedAction,
        CountryIdentificationResponse identificationResponse,
        String disclaimer,
        Integer confidence,
        List<String> blogLinks,
        boolean aiAvailable
) {

    public static EmergencyChatAdviceResponse of(
            String content, String summary, String recommendedAction,
            CountryIdentificationResponse identificationResponse,
            String disclaimer, Integer confidence, List<String> blogLinks) {
        return new EmergencyChatAdviceResponse(
                content, summary, recommendedAction, identificationResponse,
                disclaimer, confidence, blogLinks, true);
    }

    public static EmergencyChatAdviceResponse unavailable(
            CountryIdentificationResponse identificationResponse) {
        return new EmergencyChatAdviceResponse(
                null,
                "AI 응급처치 가이드를 일시적으로 제공할 수 없습니다.",
                "아래 응급 번호로 즉시 연락하십시오.",
                identificationResponse,
                "본 서비스는 전문 의료 처치를 대신할 수 없습니다.",
                null,
                List.of(),
                false);
    }
}
