package com.vitaltrip.vitaltrip.presentation.location.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BigDataCloudResponse(
    String countryName,
    String countryCode,
    String principalSubdivision,
    String locality,
    Double latitude,
    Double longitude,
    String localityLanguageRequested
) {

}
