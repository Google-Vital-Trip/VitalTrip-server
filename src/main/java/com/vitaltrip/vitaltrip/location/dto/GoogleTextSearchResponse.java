package com.vitaltrip.vitaltrip.location.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleTextSearchResponse(
        List<Place> places
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Place(
            DisplayName displayName,
            String formattedAddress,
            Location location,
            String nationalPhoneNumber,
            CurrentOpeningHours currentOpeningHours,
            String websiteUri
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DisplayName(
            String text,
            String languageCode
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentOpeningHours(
            Boolean openNow,
            List<Period> periods,
            List<String> weekdayDescriptions
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Period(
            TimeOfDay open,
            TimeOfDay close
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TimeOfDay(
            Integer hour,
            Integer minute,
            Integer day  // 0=일요일, 1=월요일, ...
    ) {
    }
}
