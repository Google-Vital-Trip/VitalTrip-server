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
    List<String> blogLinks
) {

    public static EmergencyChatAdviceResponse of(String content, String summary,
        String recommendedAction,
        CountryIdentificationResponse identificationResponse,
        String disclaimer,
        Integer confidence,
        List<String> blogLinks) {
        return new EmergencyChatAdviceResponse(content, summary, recommendedAction,
            identificationResponse, disclaimer, confidence, blogLinks);
    }
}
