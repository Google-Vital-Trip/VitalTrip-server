package com.vitaltrip.vitaltrip.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EmergencyContact(
    @Schema(description = "소방서 번호", example = "119")
    String fire,

    @Schema(description = "경찰서 번호", example = "112")
    String police,

    @Schema(description = "응급의료 번호", example = "119")
    String medical,

    @Schema(description = "통합 응급 번호 (있는 경우)", example = "911")
    String general
) {

    public static EmergencyContact of(String fire, String police, String medical) {
        return new EmergencyContact(fire, police, medical, null);
    }

    public static EmergencyContact of(String fire, String police, String medical, String general) {
        return new EmergencyContact(fire, police, medical, general);
    }
}
