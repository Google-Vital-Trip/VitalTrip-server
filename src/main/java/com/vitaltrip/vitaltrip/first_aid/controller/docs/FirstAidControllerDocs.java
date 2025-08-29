package com.vitaltrip.vitaltrip.first_aid.controller.docs;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.first_aid.dto.EmergencyChatAdviceRequest;
import com.vitaltrip.vitaltrip.first_aid.dto.EmergencyChatAdviceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "응급처치", description = "AI 기반 응급처치 조언 서비스 API")
public interface FirstAidControllerDocs {

    @Operation(
        summary = "응급처치 조언 생성",
        description = """
            응급상황 유형, 위치, 사용자 메시지를 기반으로 AI가 즉시 취해야 할 응급처치 조언을 생성합니다.
            
            ## 지원되는 응급상황 유형
            - **BLEEDING**: 출혈
            - **BURNS**: 화상  
            - **FRACTURE**: 골절
            - **ALLERGIC_REACTION**: 알레르기 반응
            - **SEIZURE**: 발작
            - **HEATSTROKE**: 열사병
            - **HYPOTHERMIA**: 저체온증
            - **POISONING**: 중독
            - **BREATHING_DIFFICULTY**: 호흡곤란
            - **ANIMAL_BITE**: 동물 물림
            - **FALL_INJURY**: 낙상 부상
            
            ## 기능 특징
            - 사용자 메시지 언어를 자동 감지하여 해당 언어로 응답
            - 위치 기반 해당 국가의 응급연락처 정보 제공
            - 증상별 특화된 응급처치 가이드 제공
            - 정확히 9-10단계의 구체적인 행동 지침
            - AI 조언 면책사항 포함
            
            ## 주의사항
            - 이는 응급상황 시 임시 조치를 위한 AI 조언입니다
            - 반드시 전문의료진의 진료를 받으시기 바랍니다
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
                    name = "출혈 응급처치 조언 예시",
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "content": "1. 안전한 곳으로 이동하세요\\n2. 상처 부위를 확인하세요\\n3. 깨끗한 천이나 거즈로 직접 압박하세요\\n4. 상처 부위를 심장보다 높게 올리세요\\n5. 압박을 계속 유지하세요\\n6. 출혈이 멈추지 않으면 추가 거즈를 덧대세요\\n7. 환자를 따뜻하게 유지하세요\\n8. 의식상태를 지속적으로 확인하세요\\n9. 119에 즉시 신고하세요\\n10. 전문의료진 도착까지 압박을 유지하세요",
                            "summary": "직접 압박과 지혈을 통해 출혈을 조절하고 쇼크를 예방해야 합니다.",
                            "recommendedAction": "119에 즉시 신고하고 상처 부위에 직접 압박을 가하세요",
                            "emergencyContact": {
                              "fire": "119",
                              "police": "112",
                              "medical": "119",
                              "general": null
                            },
                            "disclaimer": "이는 AI의 임시 응급처치 조언입니다. 참고만 하시고 빠른 시간 내에 전문의에게 상담받으세요."
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 - 필수 필드 누락 또는 유효성 검증 실패",
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
            description = "서버 오류 - AI 서비스 또는 위치 서비스 응답 실패",
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
    ApiResponse<EmergencyChatAdviceResponse> getEmergencyChatAdvice(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "응급처치 조언 요청 정보 (증상 유형, 상세 설명, 위치 좌표)",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EmergencyChatAdviceRequest.class),
                examples = {
                    @ExampleObject(
                        name = "출혈 상황",
                        summary = "계단에서 넘어져 다리 출혈",
                        value = """
                            {
                              "symptomType": "BLEEDING",
                              "symptomDetail": "계단에서 넘어져서 다리에서 피가 많이 나고 있어요. 어떻게 해야 할까요?",
                              "latitude": 37.5665,
                              "longitude": 126.9780
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "화상 상황",
                        summary = "뜨거운 물에 의한 화상",
                        value = """
                            {
                              "symptomType": "BURNS",
                              "symptomDetail": "I got burned by hot water on my hand. It's very painful and red.",
                              "latitude": 40.7580,
                              "longitude": -73.9855
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "골절 상황",
                        summary = "넘어져서 팔 골절 의심",
                        value = """
                            {
                              "symptomType": "FRACTURE",
                              "symptomDetail": "転んで腕が痛くて動かせません。骨折したかもしれません。",
                              "latitude": 35.6588,
                              "longitude": 139.7006
                            }
                            """
                    )
                }
            )
        )
        @Valid @RequestBody EmergencyChatAdviceRequest request);

    @Operation(
        summary = "회원/비회원 구분 테스트",
        description = """
            토큰 유무에 따라 다른 응답을 반환하는 테스트 엔드포인트입니다.
            
            - **토큰 없음**: 비회원으로 응급처치 서비스 이용 가능
            - **토큰 있음**: 회원 정보와 함께 응급처치 서비스 이용
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "테스트 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "비회원 응답",
                        summary = "토큰 없이 요청한 경우",
                        value = """
                            {
                              "message": "성공",
                              "data": {
                                "userType": "ANONYMOUS",
                                "description": "비회원으로 응급처치 서비스에 접근했습니다."
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "회원 응답",
                        summary = "토큰과 함께 요청한 경우",
                        value = """
                            {
                              "message": "성공",
                              "data": {
                                "userType": "AUTHENTICATED",
                                "description": "인증된 회원으로 응급처치 서비스에 접근했습니다.",
                                "userInfo": {
                                  "id": 1,
                                  "email": "test@example.com",
                                  "name": "홍길동",
                                  "countryCode": "KR",
                                  "phoneNumber": "+821012345678"
                                }
                              }
                            }
                            """
                    )
                }
            )
        )
    })
    ApiResponse<Map<String, Object>> testAuthentication(
        @Parameter(hidden = true) @AuthenticationPrincipal Object principal);
}
