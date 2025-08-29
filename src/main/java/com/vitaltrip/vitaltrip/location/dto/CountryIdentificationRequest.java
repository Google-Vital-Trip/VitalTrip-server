package com.vitaltrip.vitaltrip.location.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CountryIdentificationRequest(
    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90에서 90 사이여야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 -90에서 90 사이여야 합니다")
    @Schema(description = "위도", example = "37.5665")
    Double latitude,

    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180에서 180 사이여야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 -180에서 180 사이여야 합니다")
    @Schema(description = "경도", example = "126.9780")
    Double longitude
) {
}
