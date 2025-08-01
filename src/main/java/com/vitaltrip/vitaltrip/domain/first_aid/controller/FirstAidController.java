package com.vitaltrip.vitaltrip.domain.first_aid.controller;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.first_aid.dto.EmergencyChatAdviceRequest;
import com.vitaltrip.vitaltrip.domain.first_aid.dto.EmergencyChatAdviceResponse;
import com.vitaltrip.vitaltrip.domain.first_aid.service.FirstAidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/first-aid")
@RequiredArgsConstructor
@Tag(name = "응급처치", description = "AI 기반 응급처치 조언 서비스 API")
public class FirstAidController {

    private final FirstAidService firstAidService;

    @PostMapping("/advice")
    @Operation(
        summary = "응급처치 조언 생성",
        description = """
            응급상황 유형과 사용자 메시지를 기반으로 AI가 즉시 취해야 할 응급처치 조언을 생성합니다.
            
            ## 지원되는 응급상황 유형
            - 차후 추가 예정입니다.
            
            ## 주의사항
            - 현재는 단순 프롬프트를 활용한 응급처치 조언만을 반환하며 일부 필드는 고정값을 포함합니다.
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "응급처치 조언 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    name = "심폐소생술 조언 예시",
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "content": "심정지 상황에서는 즉시 다음과 같이 행동하세요:\\n\\n1. **119 신고**: 먼저 119에 즉시 신고하거나 주변 사람에게 신고를 요청하세요.\\n\\n2. **심폐소생술 시작**:\\n   - 환자를 단단한 바닥에 눕히세요\\n   - 가슴 중앙(젖꼭지 사이)에 손바닥 뒤꿈치를 올려놓으세요\\n   - 양손을 깍지 끼고 팔을 쭉 펴서 수직으로 압박하세요\\n   - 분당 100-120회 속도로 5-6cm 깊이로 강하게 압박하세요\\n\\n3. **지속적인 압박**: 전문의료진이 도착할 때까지 중단 없이 계속하세요.",
                            "recommendedAction": "temp",
                            "confidence": 100.0,
                            "blogLinks": [
                              "https://www.eunwoo-levi.blog/"
                            ]
                          },
                          "errorCode": null
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 - 필수 필드 누락",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "입력값 검증에 실패했습니다.",
                          "data": null,
                          "errorCode": "VALIDATION_FAILED"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "서버 오류 - AI 서비스 응답 실패",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "Gemini API 호출 실패: 네트워크 오류",
                          "data": null,
                          "errorCode": "INTERNAL_SERVER_ERROR"
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<EmergencyChatAdviceResponse> getEmergencyChatAdvice(
        @Valid @RequestBody EmergencyChatAdviceRequest request) {
        EmergencyChatAdviceResponse response = firstAidService.generateEmergencyAdvice(request);
        return ApiResponse.success(response);
    }

}
