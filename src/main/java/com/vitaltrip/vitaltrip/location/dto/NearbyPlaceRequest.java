package com.vitaltrip.vitaltrip.location.dto;

public record NearbyPlaceRequest(
        Location location,
        String type,
        Double radius,
        String language
) {
}
