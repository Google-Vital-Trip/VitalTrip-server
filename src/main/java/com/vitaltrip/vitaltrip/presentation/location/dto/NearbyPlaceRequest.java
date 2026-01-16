package com.vitaltrip.vitaltrip.presentation.location.dto;

public record NearbyPlaceRequest(
        Location location,
        String type,
        Double radius,
        String language
) {
}
