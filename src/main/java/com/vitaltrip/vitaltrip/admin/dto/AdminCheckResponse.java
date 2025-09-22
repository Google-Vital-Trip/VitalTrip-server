package com.vitaltrip.vitaltrip.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AdminCheckResponse(
    @Schema(description = "관리자 권한 여부", example = "true")
    boolean isAdmin
) {

}
