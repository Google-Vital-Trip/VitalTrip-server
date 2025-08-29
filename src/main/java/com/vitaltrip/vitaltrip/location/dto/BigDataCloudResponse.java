package com.vitaltrip.vitaltrip.location.dto;

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
