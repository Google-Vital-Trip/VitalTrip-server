package com.vitaltrip.vitaltrip.location.controller.docs;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.location.dto.NearbyPlaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "위치 기반 서비스", description = "주변 병원, 약국, 응급실 검색 API")
public interface LocationControllerDocs {

    @Operation(
            summary = "주변 의료시설 검색",
            description = """
                    현재 위치 기반으로 주변 병원, 약국, 응급실을 검색합니다.
                    
                    ## 🏥 지원 시설 타입
                    - `hospital`: 병원
                    - `pharmacy`: 약국
                    - `emergency`: 응급실
                    
                    ## 🌍 언어 코드 지원
                    Google Places API가 지원하는 모든 언어 코드를 사용할 수 있습니다:
                    - `ko`: 한국어
                    - `en`: 영어 (기본값)
                    - `ja`: 일본어
                    - `zh`: 중국어
                    - `fr`: 프랑스어
                    - `es`: 스페인어
                    - `de`: 독일어
                    - `ar`: 아랍어
                    - `hi`: 힌디어
                    - `th`: 태국어
                    - `vi`: 베트남어
                    - 기타 ISO 639-1 언어 코드
                    
                    ## 📍 검색 반경
                    - 최소: 500m
                    - 최대: 10,000m (10km)
                    - 기본값: 5,000m (5km)
                    
                    ## ⚡ 응급상황 특별 기능
                    - 결과는 거리순으로 정렬됩니다
                    - 동물병원은 자동으로 필터링됩니다
                    - 24시간 운영 여부를 확인할 수 있습니다
                    - 각 시설의 이미지 URL이 포함됩니다 (가능한 경우)
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "주변 의료시설 검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "성공",
                                              "data": [
                                                {
                                                  "name": "서울대학교병원",
                                                  "address": "서울특별시 종로구 대학로 101",
                                                  "phoneNumber": "+82-2-2072-2114",
                                                  "latitude": 37.5796,
                                                  "longitude": 126.9968,
                                                  "distance": 1200.5,
                                                  "openNow": true,
                                                  "openingHours": [
                                                    "월요일: 오전 8:30~오후 5:30",
                                                    "화요일: 오전 8:30~오후 5:30"
                                                  ],
                                                  "websiteUrl": "https://www.snuh.org"
                                                },
                                                {
                                                  "name": "세브란스병원",
                                                  "address": "서울특별시 서대문구 연세로 50-1",
                                                  "phoneNumber": "+82-2-2228-5800",
                                                  "latitude": 37.5626,
                                                  "longitude": 126.9397,
                                                  "distance": 2800.3,
                                                  "openNow": false,
                                                  "openingHours": [
                                                    "월요일: 오전 8:00~오후 5:00"
                                                  ],
                                                  "websiteUrl": "https://www.severance.healthcare"
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 파라미터",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "위도는 -90에서 90 사이여야 합니다",
                                              "errorCode": "VALIDATION_FAILED"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "외부 API 호출 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Google Places API 호출 실패: 네트워크 오류",
                                              "errorCode": "INTERNAL_SERVER_ERROR"
                                            }
                                            """
                            )
                    )
            )
    })
    ApiResponse<List<NearbyPlaceResponse>> searchNearbyPlaces(
            @Parameter(
                    description = "현재 위치의 위도",
                    example = "37.5665",
                    required = true
            )
            @RequestParam
            @DecimalMin(value = "-90.0", message = "위도는 -90에서 90 사이여야 합니다")
            @DecimalMax(value = "90.0", message = "위도는 -90에서 90 사이여야 합니다")
            Double latitude,

            @Parameter(
                    description = "현재 위치의 경도",
                    example = "126.9780",
                    required = true
            )
            @RequestParam
            @DecimalMin(value = "-180.0", message = "경도는 -180에서 180 사이여야 합니다")
            @DecimalMax(value = "180.0", message = "경도는 -180에서 180 사이여야 합니다")
            Double longitude,

            @Parameter(
                    description = "검색할 시설 타입 (hospital: 병원, pharmacy: 약국, emergency: 응급실)",
                    example = "hospital"
            )
            @RequestParam(defaultValue = "hospital")
            @Pattern(
                    regexp = "^(hospital|pharmacy|emergency)$",
                    message = "시설 타입은 hospital, pharmacy, emergency 중 하나여야 합니다"
            )
            String type,

            @Parameter(
                    description = "검색 반경 (미터 단위, 500m~10km)",
                    example = "10000"
            )
            @RequestParam(defaultValue = "5000")
            @Min(value = 500, message = "검색 반경은 최소 500m 이상이어야 합니다")
            @Max(value = 10000, message = "검색 반경은 최대 10km 이하여야 합니다")
            Double radius,

            @Parameter(
                    description = """
                            응답 언어 코드 (ISO 639-1 표준)
                            주요 지원 언어: ko(한국어), en(영어), ja(일본어), zh(중국어),
                            fr(프랑스어), es(스페인어), de(독일어), ar(아랍어), hi(힌디어),
                            th(태국어), vi(베트남어) 등
                            """,
                    example = "en"
            )
            @RequestParam(defaultValue = "en")
            String language
    );
}
