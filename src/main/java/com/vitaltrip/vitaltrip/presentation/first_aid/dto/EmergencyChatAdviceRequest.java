package com.vitaltrip.vitaltrip.presentation.first_aid.dto;

import com.vitaltrip.vitaltrip.domain.first_aid.EmergencySymptomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record EmergencyChatAdviceRequest(

    @NotNull(message = "증상 유형은 필수입니다")
    @Schema(description = "응급상황 증상 유형", example = "BLEEDING")
    EmergencySymptomType symptomType,

    @NotBlank(message = "증상 설명은 필수입니다")
    @Size(max = 1000, message = "증상 설명은 1000자 이하여야 합니다")
    @Schema(description = "응급상황에 대한 상세 설명", example = "계단에서 넘어져서 다리에서 피가 많이 나고 있어요")
    String symptomDetail,

    @NotNull(message = "위도는 필수입니다")
    @DecimalMin(value = "-90.0", message = "위도는 -90에서 90 사이여야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 -90에서 90 사이여야 합니다")
    @Schema(description = "현재 위치의 위도", example = "37.5665")
    Double latitude,

    @NotNull(message = "경도는 필수입니다")
    @DecimalMin(value = "-180.0", message = "경도는 -180에서 180 사이여야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 -180에서 180 사이여야 합니다")
    @Schema(description = "현재 위치의 경도", example = "126.9780")
    Double longitude

) {

}
