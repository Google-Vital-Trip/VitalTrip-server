package com.vitaltrip.vitaltrip.location.controller.docs;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.location.dto.CountryIdentificationRequest;
import com.vitaltrip.vitaltrip.location.dto.CountryIdentificationResponse;
import com.vitaltrip.vitaltrip.location.dto.NearbyPlaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "위치 기반 서비스", description = "주변 병원, 약국, 응급실 검색 및 국가 식별 API")
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

    @Operation(
        summary = "위경도 기반 국가 식별 및 응급 연락처 조회",
        description = """
            입력받은 위도와 경도를 기반으로 해당 위치의 국가를 식별하고, 해당 국가의 응급 연락처 정보를 함께 제공합니다.
            
            ## 🌍 BigDataCloud API 활용
            - 무료 클라이언트 사이드 API 사용
            - 전세계 모든 좌표 지원 (바다, 해양 포함)
            - ISO 3166-1 alpha-2 국가 코드 반환
            
            ## 📍 좌표 요구사항
            - **위도**: -90.0 ~ 90.0 사이의 값
            - **경도**: -180.0 ~ 180.0 사이의 값
            - 소수점 이하 6자리까지 지원
            
            ## 🚨 응급 연락처 정보
            - 각 국가별 소방서, 경찰서, 응급의료, 통합 응급번호 제공
            - 70+ 개국 지원 (아시아-태평양, 북미, 유럽, 중동, 아프리카, 남미)
            - 해당 국가의 응급 연락처가 없을 경우 null 반환
            
            ## 🔍 응답 정보
            - ISO 3166-1 alpha-2 국가 코드 (예: KR, US, JP)
            - 국가명 (영어)
            - 입력된 좌표 정보
            - 해당 국가의 응급 연락처 정보 (소방서, 경찰서, 응급의료, 통합)
            
            ## 🚀 사용 사례
            - 여행자의 현재 위치 국가 확인
            - 응급상황 시 관할 국가의 응급 연락처 제공
            - 지역별 응급 서비스 안내
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "국가 식별 및 응급 연락처 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "한국 좌표",
                        summary = "서울역 좌표 예시 (응급 연락처 포함)",
                        value = """
                            {
                              "message": "성공",
                              "data": {
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
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "미국 좌표",
                        summary = "뉴욕 타임스퀘어 좌표 예시 (911 통합번호)",
                        value = """
                            {
                              "message": "성공",
                              "data": {
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
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "일본 좌표",
                        summary = "도쿄 시부야 좌표 예시",
                        value = """
                            {
                              "message": "성공",
                              "data": {
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
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "응급 연락처 미지원 국가",
                        summary = "응급 연락처 정보가 없는 국가의 경우",
                        value = """
                            {
                              "message": "성공",
                              "data": {
                                "countryCode": "XX",
                                "countryName": "Unknown Country",
                                "latitude": 0.0,
                                "longitude": 0.0,
                                "emergencyContact": null
                              }
                            }
                            """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 좌표 값",
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
            description = "BigDataCloud API 호출 실패",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "BigDataCloud API 호출 실패: 네트워크 오류",
                          "errorCode": "INTERNAL_SERVER_ERROR"
                        }
                        """
                )
            )
        )
    })
    ApiResponse<CountryIdentificationResponse> identifyCountry(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "국가 식별을 위한 좌표 정보",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "서울역",
                        summary = "서울역 좌표",
                        value = """
                            {
                              "latitude": 37.5665,
                              "longitude": 126.9780
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "뉴욕",
                        summary = "뉴욕 타임스퀘어 좌표",
                        value = """
                            {
                              "latitude": 40.7580,
                              "longitude": -73.9855
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "도쿄",
                        summary = "도쿄 시부야 좌표",
                        value = """
                            {
                              "latitude": 35.6588,
                              "longitude": 139.7006
                            }
                            """
                    )
                }
            )
        )
        @Valid @RequestBody CountryIdentificationRequest request
    );
}
