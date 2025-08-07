package com.vitaltrip.vitaltrip.domain.first_aid.dto;

import java.util.List;

public record EmergencyChatAdviceResponse(
        String content,
        String summary,
        String recommendedAction,
        double confidence,
        List<String> blogLinks
) {

    public static EmergencyChatAdviceResponse from(String content, String summary,
                                                   String recommendedAction,
                                                   double confidence, List<String> blogLinks) {
        return new EmergencyChatAdviceResponse(content, summary, recommendedAction, confidence,
                blogLinks);
    }
}
