package com.vitaltrip.vitaltrip.presentation.health;

import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "상태 확인", description = "서버 상태 확인 API")
public class HealthController {

    @GetMapping("/")
    @Operation(summary = "기본 경로", description = "API 서버 정보")
    public ApiResponse<Map<String, Object>> home() {
        return ApiResponse.success(Map.of(
                "service", "VitalTrip API",
                "version", "0.0.1",
                "docs", "/swagger-ui.html"
        ));
    }

}
