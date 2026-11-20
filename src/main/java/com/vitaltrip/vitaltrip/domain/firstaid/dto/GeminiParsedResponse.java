package com.vitaltrip.vitaltrip.domain.firstaid.dto;

public record GeminiParsedResponse(
    String content,
    String summary,
    String recommendedAction,
    String disclaimer
) {

}

