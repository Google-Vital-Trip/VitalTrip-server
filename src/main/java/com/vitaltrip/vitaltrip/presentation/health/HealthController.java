package com.vitaltrip.vitaltrip.presentation.health;

import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "상태 확인", description = "서버 상태 확인 API")
public class HealthController {

    @GetMapping("/")
    @Operation(summary = "기본 경로", description = "서버가 정상적으로 실행 중인지 확인")
    public ApiResponse<Map<String, Object>> home() {
        Map<String, Object> info = Map.of(
            "service", "VitalTrip API",
            "status", "running",
            "timestamp", LocalDateTime.now(),
            "message", "Welcome to VitalTrip API! 🌍✈️"
        );
        return ApiResponse.success(info);
    }

    @GetMapping("/health")
    @Hidden
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> status = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString()
        );
        return ApiResponse.success(status);
    }
}
