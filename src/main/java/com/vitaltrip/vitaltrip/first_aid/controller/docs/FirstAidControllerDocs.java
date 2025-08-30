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
            
            ## 🔥 지원되는 응급상황 유형
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
            
            ## 🚀 AI 기능 특징
            - **언어 자동 감지**: 사용자 메시지의 언어를 자동으로 감지하여 동일한 언어로 응답
            - **위치 기반 서비스**: 좌표를 통해 해당 국가의 응급연락처 정보 자동 제공
            - **증상별 특화 가이드**: 각 응급상황 유형에 맞는 전문적인 처치 절차 제공
            - **구조화된 응답**: 9-10단계의 명확한 행동 지침과 요약, 권장 조치사항
            - **신뢰도 평가**: AI 응답의 품질을 0-100점으로 평가하여 제공
            - **관련 리소스**: 해당 증상과 관련된 신뢰할 수 있는 블로그 및 의료 정보 링크 제공
            
            ## 🌍 다국어 지원
            - **자동 언어 감지**: 문법 패턴 분석으로 정확한 언어 식별
            - **지원 언어**: 한국어, 영어, 일본어, 중국어, 스페인어, 프랑스어, 독일어, 아랍어, 힌디어, 러시아어 등
            - **일관된 응답**: 모든 섹션(content, summary, recommendedAction, disclaimer)이 동일 언어로 제공
            
            ## 📍 위치 기반 국가 식별
            - **BigDataCloud API**: 전세계 좌표 → 국가 정보 변환
            - **70+ 개국 응급연락처**: 소방서, 경찰서, 응급의료, 통합 응급번호 제공
            - **실시간 응급번호**: 해당 국가의 현지 응급연락처 즉시 제공
            
            ## ⚠️ 중요 안전 고지
            - 이는 응급상황 시 임시 조치를 위한 AI 조언입니다
            - 반드시 전문의료진의 진료를 받으시기 바랍니다
            - 생명이 위급한 상황에서는 즉시 응급서비스(119, 911 등)에 신고하세요
            
            ## 🔓 접근 권한
            - **회원**: 토큰과 함께 요청하면 사용자 정보가 로그에 기록됩니다
            - **비회원**: 토큰 없이도 모든 기능을 동일하게 이용할 수 있습니다
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "응급처치 조언 생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = {
                    @ExampleObject(
                        name = "한국어 출혈 응급처치 조언",
                        summary = "한국 서울에서 발생한 출혈 상황",
                        value = """
                            {
                              "message": "성공",
                              "data": {
                                "content": "안전한 곳으로 이동하세요\\n상처 부위를 신속하게 확인하세요\\n깨끗한 천이나 거즈로 상처에 직접 압박을 가하세요\\n상처 부위를 심장보다 높게 올려주세요\\n압박을 지속적으로 유지하세요\\n출혈이 멈추지 않으면 추가 거즈를 덧대세요\\n환자를 따뜻하게 유지해주세요\\n의식상태를 지속적으로 확인하세요\\n119에 즉시 신고하세요\\n전문의료진 도착까지 압박을 계속 유지하세요",
                                "summary": "직접 압박과 지혈을 통해 출혈을 조절하고 쇼크를 예방해야 합니다.",
                                "recommendedAction": "119에 즉시 신고하고 상처 부위에 직접 압박을 가하세요",
                                "identificationResponse": {
                                  "countryCode": "KR",
                                  "countryName": "South Korea", 
                                  "latitude": 37.5665,
                                  "longitude": 126.9780,
                                  "emergencyContact": {
                                    "fire": "119",
                                    "police": "112",
                                    "medical": "119",
                                    "general": "112"
                                  }
                                },
                                "disclaimer": "이는 AI의 임시 응급처치 조언입니다. 참고만 하시고 빠른 시간 내에 전문의에게 상담받으세요.",
                                "confidence": 92,
                                "blogLinks": [
                                  "https://www.119.go.kr/webapp/ptl/ptl010/ptl010_010100/ptl010_010100050/ptl010_010100050010/ptl010_010100050010.jsp",
                                  "https://www.redcross.or.kr/webapp/homepage/hp30100/hp30100.jsp?menuId=HP30101",
                                  "https://blog.naver.com/redcross_blog/221234567890"
                                ]
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "영어 화상 응급처치 조언",
                        summary = "미국 뉴욕에서 발생한 화상 상황",
                        value = """
                            {
                              "message": "성공", 
                              "data": {
                                "content": "Move away from the heat source immediately\\nRemove any hot clothing or jewelry carefully\\nCool the burn with running cold water for 10-15 minutes\\nDo not use ice directly on the burn\\nGently pat the area dry with a clean cloth\\nApply a sterile gauze bandage loosely\\nDo not apply butter, oil, or home remedies\\nTake over-the-counter pain medication if needed\\nCall 911 for severe burns or if unsure\\nKeep the person calm and monitor for shock",
                                "summary": "Immediate cooling and proper burn care are essential to prevent further damage.",
                                "recommendedAction": "Call 911 immediately and cool the burn with running water",
                                "identificationResponse": {
                                  "countryCode": "US",
                                  "countryName": "United States",
                                  "latitude": 40.7580,
                                  "longitude": -73.9855,
                                  "emergencyContact": {
                                    "fire": "911",
                                    "police": "911", 
                                    "medical": "911",
                                    "general": "911"
                                  }
                                },
                                "disclaimer": "This is temporary AI first aid advice. Please seek professional medical care immediately.",
                                "confidence": 89,
                                "blogLinks": [
                                  "https://www.redcross.org/get-help/how-to-prepare-for-emergencies/types-of-emergencies/burns",
                                  "https://www.mayoclinic.org/first-aid/first-aid-burns/basics/art-20056649",
                                  "https://medlineplus.gov/firstaid.html"
                                ]
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "일본어 골절 응급처치 조언",
                        summary = "일본 도쿄에서 발생한 골절 상황",
                        value = """
                            {
                              "message": "성공",
                              "data": {
                                "content": "安全な場所に移動してください\\n骨折部位を確認してください\\n患部を動かさないようにしてください\\n添え木で骨折部位を固定してください\\n包帯で固定を強化してください\\n患部を心臓より高く上げてください\\n氷で冷やして腫れを抑えてください\\n痛み止めを服用してください\\n119番に通報してください\\n専門医の到着まで安静にしてください",
                                "summary": "骨折部位の固定と腫れの抑制が重要です。",
                                "recommendedAction": "119番に通報し、患部を固定してください",
                                "identificationResponse": {
                                  "countryCode": "JP",
                                  "countryName": "Japan",
                                  "latitude": 35.6588,
                                  "longitude": 139.7006,
                                  "emergencyContact": {
                                    "fire": "119",
                                    "police": "110",
                                    "medical": "119",
                                    "general": null
                                  }
                                },
                                "disclaimer": "これはAIによる応急処置のアドバイスです。必ず専門医にご相談ください。",
                                "confidence": 87,
                                "blogLinks": [
                                  "https://www.jrc.or.jp/activity/study/safety/rescue/knowledge/fracture/",
                                  "https://www.fdma.go.jp/mission/prevention/kinkyu/kq9003004.html",
                                  "https://www.mayoclinic.org/first-aid/first-aid-fractures/basics/art-20056641"
                                ]
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "응급연락처 미지원 국가",
                        summary = "응급연락처 정보가 없는 지역의 경우",
                        value = """
                            {
                              "message": "성공",
                              "data": {
                                "content": "Move to a safe location\\nAssess the severity of the situation\\nApply basic first aid principles\\nCall local emergency services\\nKeep the person conscious and comfortable\\nMonitor vital signs regularly\\nProvide reassurance to the victim\\nWait for professional medical help\\nDocument what happened for medical personnel\\nStay calm and follow safety protocols",
                                "summary": "Basic first aid principles should be applied while seeking professional help.",
                                "recommendedAction": "Contact local emergency services immediately",
                                "identificationResponse": {
                                  "countryCode": "XX",
                                  "countryName": "Unknown Location",
                                  "latitude": 0.0,
                                  "longitude": 0.0,
                                  "emergencyContact": null
                                },
                                "disclaimer": "This is temporary AI first aid advice. Please seek professional medical care immediately.",
                                "confidence": 75,
                                "blogLinks": [
                                  "https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public",
                                  "https://www.redcross.org/get-help/how-to-prepare-for-emergencies/anatomy-of-a-first-aid-kit",
                                  "https://medlineplus.gov/firstaid.html"
                                ]
                              }
                            }
                            """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 - 필수 필드 누락 또는 유효성 검증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "필수 필드 누락",
                        value = """
                            {
                              "message": "입력값 검증에 실패했습니다.",
                              "data": null,
                              "errorCode": "VALIDATION_FAILED"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "잘못된 좌표 값",
                        value = """
                            {
                              "message": "위도는 -90에서 90 사이여야 합니다",
                              "data": null,
                              "errorCode": "VALIDATION_FAILED"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "증상 설명 길이 초과",
                        value = """
                            {
                              "message": "증상 설명은 1000자 이하여야 합니다",
                              "data": null,
                              "errorCode": "VALIDATION_FAILED"
                            }
                            """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "서버 오류 - AI 서비스 또는 위치 서비스 응답 실패",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Gemini API 오류",
                        value = """
                            {
                              "message": "Gemini API 호출 실패: 네트워크 오류",
                              "data": null,
                              "errorCode": "INTERNAL_SERVER_ERROR"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "BigDataCloud API 오류",
                        value = """
                            {
                              "message": "BigDataCloud API 호출 실패: 서비스 일시 중단",
                              "data": null,
                              "errorCode": "INTERNAL_SERVER_ERROR"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "AI 응답 파싱 오류",
                        value = """
                            {
                              "message": "컨텐츠 생성 실패: AI 응답 파싱 중 오류가 발생했습니다",
                              "data": null,
                              "errorCode": "INTERNAL_SERVER_ERROR"
                            }
                            """
                    )
                }
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
                        name = "출혈 상황 (한국어)",
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
                        name = "화상 상황 (영어)",
                        summary = "뜨거운 물에 의한 화상",
                        value = """
                            {
                              "symptomType": "BURNS",
                              "symptomDetail": "I got burned by hot water on my hand. It's very painful and red. What should I do?",
                              "latitude": 40.7580,
                              "longitude": -73.9855
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "골절 상황 (일본어)",
                        summary = "넘어져서 팔 골절 의심",
                        value = """
                            {
                              "symptomType": "FRACTURE",
                              "symptomDetail": "転んで腕が痛くて動かせません。骨折したかもしれません。助けてください。",
                              "latitude": 35.6588,
                              "longitude": 139.7006
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "알레르기 반응 (스페인어)",
                        summary = "음식 알레르기 반응",
                        value = """
                            {
                              "symptomType": "ALLERGIC_REACTION", 
                              "symptomDetail": "Comí mariscos y ahora tengo picazón y dificultad para respirar. ¿Qué debo hacer?",
                              "latitude": 40.4165,
                              "longitude": -3.7026
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "발작 상황 (프랑스어)",
                        summary = "간질 발작 목격",
                        value = """
                            {
                              "symptomType": "SEIZURE",
                              "symptomDetail": "Une personne fait une crise d'épilepsie devant moi. Comment puis-je l'aider?",
                              "latitude": 48.8566,
                              "longitude": 2.3522
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "열사병 (중국어)",
                        summary = "더운 날씨로 인한 열사병",
                        value = """
                            {
                              "symptomType": "HEATSTROKE",
                              "symptomDetail": "天气很热，我感到头晕恶心，体温很高。我该怎么办？",
                              "latitude": 39.9042,
                              "longitude": 116.4074
                            }
                            """
                    )
                }
            )
        )
        @Valid @RequestBody EmergencyChatAdviceRequest request);

    @Operation(
        summary = "응급처치 서비스 인증 테스트",
        description = """
            토큰 유무에 따라 다른 응답을 반환하는 테스트 엔드포인트입니다.
            응급처치 서비스의 인증 시스템이 올바르게 동작하는지 확인할 수 있습니다.
            
            ## 🔍 인증 방식
            - **토큰 없음**: 비회원으로 응급처치 서비스 이용 가능
            - **유효한 토큰**: 회원 정보와 함께 응급처치 서비스 이용
            - **임시 토큰**: OAuth2 프로필 완성 중인 사용자도 이용 가능
            
            ## 🚨 응급처치 서비스 특징
            - **무조건 접근 허용**: 생명과 직결된 서비스이므로 인증 상태와 관계없이 모든 사용자가 이용 가능
            - **이중 인증 필터**: `FirstAidAuthenticationFilter`가 별도로 처리
            - **사용자 구분**: 로그 기록 및 통계를 위해 회원/비회원 구분
            
            ## 📊 응답 정보
            - **AUTHENTICATED**: 정상 로그인된 회원 (사용자 정보 포함)
            - **ANONYMOUS**: 비회원 사용자
            - **UNKNOWN**: 알 수 없는 인증 상태 (디버깅용)
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "인증 테스트 성공",
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
                                "description": "비회원으로 접근했습니다."
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "일반 회원 응답",
                        summary = "정상 토큰으로 요청한 경우",
                        value = """
                            {
                              "message": "성공",
                              "data": {
                                "userType": "AUTHENTICATED",
                                "description": "인증된 회원으로 접근했습니다.",
                                "userInfo": {
                                  "id": 1,
                                  "email": "user@example.com",
                                  "name": "홍길동",
                                  "countryCode": "KR",
                                  "phoneNumber": "+821012345678"
                                }
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "OAuth2 사용자 응답",
                        summary = "소셜 로그인 사용자의 경우",
                        value = """
                            {
                              "message": "성공",
                              "data": {
                                "userType": "AUTHENTICATED",
                                "description": "인증된 회원으로 접근했습니다.",
                                "userInfo": {
                                  "id": 2,
                                  "email": "social@gmail.com", 
                                  "name": "김철수",
                                  "countryCode": "US",
                                  "phoneNumber": "+12345678901"
                                }
                              }
                            }
                            """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "이 엔드포인트에서는 401 오류가 발생하지 않습니다",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "note": "응급처치 서비스는 생명과 직결된 서비스이므로 인증 실패 시에도 비회원으로 처리하여 서비스를 제공합니다."
                        }
                        """
                )
            )
        )
    })
    ApiResponse<Map<String, Object>> testAuthentication(
        @Parameter(
            description = "인증된 사용자 정보 (토큰이 있을 경우)",
            hidden = true
        )
        @AuthenticationPrincipal Object principal);
}
