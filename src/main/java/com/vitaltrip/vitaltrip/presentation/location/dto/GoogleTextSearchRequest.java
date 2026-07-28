package com.vitaltrip.vitaltrip.presentation.location.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleTextSearchRequest(
        String textQuery,
        LocationBias locationBias,
        Integer maxResultCount,
        String languageCode,
        Boolean openNow
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LocationBias(
            Circle circle
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Circle(
            Location center,
            Double radius
    ) {
    }
}
