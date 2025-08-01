package com.vitaltrip.vitaltrip.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {

    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {

        private Content content;
        private String finishReason;
        private Integer index;
        private List<SafetyRating> safetyRatings;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {

        private List<Part> parts;
        private String role;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {

        private String text;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SafetyRating {

        private String category;
        private String probability;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageMetadata {

        private Integer promptTokenCount;
        private Integer candidatesTokenCount;
        private Integer totalTokenCount;
    }

    public String getGeneratedText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate firstCandidate = candidates.getFirst();
            if (firstCandidate.getContent() != null &&
                firstCandidate.getContent().getParts() != null &&
                !firstCandidate.getContent().getParts().isEmpty()) {
                return firstCandidate.getContent().getParts().getFirst().getText();
            }
        }
        return null;
    }
}
