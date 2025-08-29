package com.vitaltrip.vitaltrip.first_aid.dto;

public record GeminiParsedResponse(
    String content,
    String summary,
    String recommendedAction,
    String disclaimer
) {

}

