package com.vitaltrip.vitaltrip.domain.first_aid.dto;

import java.util.List;

public record EmergencyChatAdviceResponse(
    String content,
    String summery,
    String recommendedAction,
    double confidence,
    List<String> blogLinks
) {

    public static EmergencyChatAdviceResponse from(String content, String summery,
        String recommendedAction,
        double confidence, List<String> blogLinks) {
        return new EmergencyChatAdviceResponse(content, summery, recommendedAction, confidence,
            blogLinks);
    }
}
