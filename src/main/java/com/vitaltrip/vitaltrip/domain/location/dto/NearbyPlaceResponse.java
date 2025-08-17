package com.vitaltrip.vitaltrip.domain.location.dto;

import java.util.List;

public record NearbyPlaceResponse(
        String name,
        String address,
        String phoneNumber,
        Double latitude,
        Double longitude,
        Double distance,
        Boolean openNow,
        List<String> openingHours,
        String websiteUrl
) {
}
