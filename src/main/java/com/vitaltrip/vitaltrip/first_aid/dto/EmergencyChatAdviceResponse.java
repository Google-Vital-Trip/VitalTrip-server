package com.vitaltrip.vitaltrip.first_aid.dto;

import com.vitaltrip.vitaltrip.location.dto.EmergencyContact;
import java.util.List;

public record EmergencyChatAdviceResponse(
    String content,
    String summary,
    String recommendedAction,
    EmergencyContact emergencyContact,
    String disclaimer,
    Integer confidence,
    List<String> blogLinks
) {

    public static EmergencyChatAdviceResponse of(String content, String summary,
        String recommendedAction,
        EmergencyContact emergencyContact,
        String disclaimer,
        Integer confidence,
        List<String> blogLinks) {
        return new EmergencyChatAdviceResponse(content, summary, recommendedAction,
            emergencyContact, disclaimer, confidence, blogLinks);
    }
}
