package com.vitaltrip.vitaltrip.domain.first_aid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmergencyChatAdviceRequest(

    @NotBlank(message = "증상 유형은 필수입니다")
    @Size(max = 50, message = "증상 유형은 50자 이하여야 합니다")
    @Schema(description = "증상 유형", example = "Cardiac Arrest")
    String symptomType,

    @NotBlank(message = "증상 설명은 필수입니다")
    @Size(max = 1000, message = "증상 설명은 1000자 이하여야 합니다")
    @Schema(description = "응급상황에 대한 상세 설명", example = "I found an unconscious person with no breathing. What should I do?")
    String symptomDetail

) {

}
