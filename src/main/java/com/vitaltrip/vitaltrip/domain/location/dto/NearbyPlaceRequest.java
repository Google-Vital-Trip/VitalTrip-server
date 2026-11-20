package com.vitaltrip.vitaltrip.domain.location.dto;

public record NearbyPlaceRequest(
        Location location,
        String type,
        Double radius,
        String language
) {
}
