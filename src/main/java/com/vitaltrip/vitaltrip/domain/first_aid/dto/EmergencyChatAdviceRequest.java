package com.vitaltrip.vitaltrip.domain.first_aid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmergencyChatAdviceRequest(

    @NotBlank(message = "응급상황 유형은 필수입니다")
    @Size(max = 50, message = "응급상황 유형은 50자 이하여야 합니다")
    @Schema(description = "응급상황 유형", example = "심정지")
    String emergencyType,

    @NotBlank(message = "사용자 메시지는 필수입니다")
    @Size(max = 1000, message = "사용자 메시지는 1000자 이하여야 합니다")
    @Schema(description = "응급상황에 대한 상세 설명", example = "의식을 잃고 호흡이 없는 사람을 발견했습니다. 어떻게 해야 하나요?")
    String userMessage

) {

}
