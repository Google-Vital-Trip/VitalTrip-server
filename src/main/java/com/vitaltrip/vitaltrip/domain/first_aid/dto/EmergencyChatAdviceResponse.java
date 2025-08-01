package com.vitaltrip.vitaltrip.domain.first_aid.dto;

import java.util.List;

public record EmergencyChatAdviceResponse(
    String content,
    String recommendedAction,
    double confidence,
    List<String> blogLinks
) {

    public static EmergencyChatAdviceResponse from(String content, String recommendedAction,
        double confidence, List<String> blogLinks) {
        return new EmergencyChatAdviceResponse(content, recommendedAction, confidence, blogLinks);
    }
}
