package com.vitaltrip.vitaltrip.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CountryIdentificationResponse(
    @Schema(description = "ISO 3166-1 alpha-2 국가 코드", example = "KR")
    String countryCode,

    @Schema(description = "국가명 (영어)", example = "South Korea")
    String countryName,

    @Schema(description = "입력된 위도", example = "37.5665")
    Double latitude,

    @Schema(description = "입력된 경도", example = "126.9780")
    Double longitude,

    @Schema(description = "국가 응급 번호",
        example = """
            {
              "fire": "119",
              "police": "112", 
              "medical": "119",
              "general": "112"
            }
            """)
    EmergencyContact emergencyContact
) {

    public static CountryIdentificationResponse from(BigDataCloudResponse response,
        Double inputLatitude,
        Double inputLongitude,
        EmergencyContact emergencyContact) {
        return new CountryIdentificationResponse(
            response.countryCode(),
            response.countryName(),
            inputLatitude,
            inputLongitude,
            emergencyContact
        );
    }
}
