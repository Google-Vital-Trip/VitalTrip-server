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
            - 현재는 단순 프롬프트를 활용한 응급처치 조언만을 반환하며 일부 필드(summery, recommendedAction, confidence, blogLinks)는 고정값을 포함합니다.
            - AI 응답은 영어로 제공됩니다.
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
                            "content": "For cardiac arrest situation, take the following actions immediately:\\n\\n1. **Call 911**: First, call 911 immediately or ask someone nearby to make the call.\\n\\n2. **Start CPR**:\\n   - Place the patient on a firm, flat surface\\n   - Position the heel of your palm on the center of the chest (between the nipples)\\n   - Interlace your fingers and keep your arms straight\\n   - Compress hard and fast at a rate of 100-120 per minute\\n   - Push at least 2 inches (5-6 cm) deep\\n\\n3. **Continue compressions**: Keep going without interruption until professional medical help arrives.",
                            "summery": "summery",
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
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "응급처치 조언 요청 정보",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmergencyChatAdviceRequest.class),
                examples = {
                    @ExampleObject(
                        name = "심정지 상황",
                        summary = "의식불명 환자 발견",
                        value = """
                            {
                              "emergencyType": "Cardiac Arrest",
                              "userMessage": "I found an unconscious person with no breathing. What should I do?"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "출혈 상황",
                        summary = "칼에 베인 상처",
                        value = """
                            {
                              "emergencyType": "Severe Bleeding",
                              "userMessage": "I cut myself with a knife and there's a lot of bleeding. How should I treat it?"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "화상 상황",
                        summary = "끓는 물에 데임",
                        value = """
                            {
                              "emergencyType": "Burns",
                              "userMessage": "I burned my hand with boiling water and blisters have formed. How should I treat it?"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "질식 상황",
                        summary = "음식물 기도 폐쇄",
                        value = """
                            {
                              "emergencyType": "Choking",
                              "userMessage": "Food is stuck in the throat and the person can't breathe. This is urgent!"
                            }
                            """
                    )
                }
            )
        )
        @Valid @RequestBody EmergencyChatAdviceRequest request) {
        EmergencyChatAdviceResponse response = firstAidService.generateEmergencyAdvice(request);
        return ApiResponse.success(response);
    }
}
