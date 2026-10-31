package com.vitaltrip.vitaltrip.infra.gemini.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiRequest(
        Content systemInstruction,
        List<Content> contents,
        GenerationConfig generationConfig
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Content(String role, List<Part> parts) {

        public static Content of(String role, String text) {
            return new Content(role, List.of(new Part(text)));
        }
    }

    public record Part(String text) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GenerationConfig(
            Double temperature,
            String responseMimeType,
            Schema responseSchema,
            ThinkingConfig thinkingConfig
    ) {
    }

    public record Schema(String type, @JsonProperty("enum") List<String> values) {

        public static Schema enumOf(List<String> values) {
            return new Schema("STRING", values);
        }
    }

    public record ThinkingConfig(String thinkingLevel) {
    }
}
