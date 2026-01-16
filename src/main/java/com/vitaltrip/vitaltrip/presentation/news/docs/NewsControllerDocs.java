package com.vitaltrip.vitaltrip.presentation.news.docs;

import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import com.vitaltrip.vitaltrip.presentation.news.dto.MedicalNewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "의료 뉴스", description = "News API를 활용한 의료 관련 뉴스 제공")
public interface NewsControllerDocs {

    @Operation(
            summary = "의료 관련 뉴스 검색",
            description = """
            News API를 활용하여 의료 관련 뉴스를 검색합니다.
            
            ## 🔍 검색 기능
            - **키워드 검색**: 특정 키워드로 의료 뉴스 검색
            - **자동 필터링**: 의료 관련 키워드 자동 추가로 관련성 향상
            - **다국어 지원**: 9개 언어 지원
            - **최신순 정렬**: 발행일 기준 최신순 정렬
            
            ## 📰 검색 범위
            검색 쿼리가 없을 경우 기본 의료 키워드로 검색:
            - health, medical, medicine, healthcare
            - hospital, doctor, treatment
            
            사용자 쿼리가 있을 경우 의료 관련 키워드와 결합하여 검색
            
            ## 🌍 지원 언어
            - en (English) - 기본값
            - ko (Korean)
            - ja (Japanese)  
            - zh (Chinese)
            - es (Spanish)
            - fr (French)
            - de (German)
            - ru (Russian)
            - ar (Arabic)
            
            ## ⚡ 응답 최적화
            - 빠른 응답을 위한 경량화된 구조
            - 필수 정보만 포함 (제목, 설명, URL, 이미지, 출처, 발행일)
            - 페이지네이션 지원
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "의료 뉴스 검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "articles": [
                              {
                                "title": "새로운 암 치료법 개발",
                                "description": "혁신적인 면역치료법이 임상시험에서 놀라운 결과를 보였습니다...",
                                "url": "https://example.com/news/cancer-treatment",
                                "imageUrl": "https://example.com/images/medical-news.jpg",
                                "sourceName": "Medical News Today",
                                "publishedAt": "2024-01-15T10:30:00"
                              },
                              {
                                "title": "AI 진단 시스템의 정확도 향상",
                                "description": "인공지능을 활용한 의료 진단 시스템이 기존 방법보다 높은 정확도를 보입니다...",
                                "url": "https://example.com/news/ai-diagnosis",
                                "imageUrl": "https://example.com/images/ai-medical.jpg",
                                "sourceName": "Healthcare Weekly",
                                "publishedAt": "2024-01-15T08:45:00"
                              }
                            ],
                            "totalResults": 1250,
                            "page": 1,
                            "pageSize": 10
                          }
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
                          "message": "페이지는 1 이상이어야 합니다",
                          "errorCode": "VALIDATION_FAILED"
                        }
                        """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "News API 호출 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "message": "News API 호출 실패: 네트워크 오류",
                          "errorCode": "INTERNAL_SERVER_ERROR"
                        }
                        """
                            )
                    )
            )
    })
    ApiResponse<MedicalNewsResponse> getMedicalNews(
            @Parameter(
                    description = "검색할 키워드 (없으면 기본 의료 키워드로 검색)",
                    example = "cancer treatment"
            )
            @RequestParam(required = false) String query,

            @Parameter(
                    description = "페이지 번호 (1부터 시작)",
                    example = "1"
            )
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
            @Max(value = 100, message = "페이지는 100 이하여야 합니다")
            int page,

            @Parameter(
                    description = "페이지당 기사 수 (1-100)",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
            @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
            int pageSize,

            @Parameter(
                    description = "언어 코드 (en, ko, ja, zh, es, fr, de, ru, ar)",
                    example = "en"
            )
            @RequestParam(defaultValue = "en")
            @Pattern(regexp = "^(en|ko|ja|zh|es|fr|de|ru|ar)$",
                    message = "지원되는 언어: en, ko, ja, zh, es, fr, de, ru, ar")
            String language
    );

    @Operation(
            summary = "건강 관련 최신 헤드라인",
            description = """
            News API의 Top Headlines 기능을 활용하여 특정 국가의 건강 관련 최신 뉴스를 제공합니다.
            
            ## 📺 헤드라인 특징
            - **최신 뉴스**: 각 국가의 가장 최신 건강 관련 헤드라인
            - **신뢰도 높음**: 주요 언론사의 검증된 뉴스
            - **국가별 맞춤**: 각 국가의 현지 상황 반영
            - **빠른 업데이트**: 실시간으로 업데이트되는 헤드라인
            
            ## 🌐 지원 국가 (주요)
            - **us**: 미국 (기본값)
            - **kr**: 한국
            - **jp**: 일본
            - **cn**: 중국
            - **gb**: 영국
            - **de**: 독일
            - **fr**: 프랑스
            - **ca**: 캐나다
            - **au**: 호주
            
            ## 💡 사용 팁
            - 국가별로 다른 언어의 뉴스가 제공될 수 있습니다
            - 일부 국가는 헤드라인이 제한적일 수 있습니다
            - 최신 뉴스 확인에 최적화되어 있습니다
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "건강 헤드라인 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "articles": [
                              {
                                "title": "CDC Issues New Guidelines for Flu Season",
                                "description": "The Centers for Disease Control and Prevention has released updated recommendations for the upcoming flu season...",
                                "url": "https://example.com/news/cdc-flu-guidelines",
                                "imageUrl": "https://example.com/images/cdc-news.jpg",
                                "sourceName": "CNN Health",
                                "publishedAt": "2024-01-15T12:00:00"
                              },
                              {
                                "title": "Breakthrough in Alzheimer's Research",
                                "description": "Scientists have made a significant breakthrough in understanding Alzheimer's disease...",
                                "url": "https://example.com/news/alzheimer-research",
                                "imageUrl": "https://example.com/images/brain-research.jpg",
                                "sourceName": "BBC Health",
                                "publishedAt": "2024-01-15T11:30:00"
                              }
                            ],
                            "totalResults": 45,
                            "page": 1,
                            "pageSize": 10
                          }
                        }
                        """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 국가 코드",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "message": "국가 코드는 2자리 소문자여야 합니다",
                          "errorCode": "VALIDATION_FAILED"
                        }
                        """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "News API 호출 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "message": "헤드라인 조회 실패: API 한도 초과",
                          "errorCode": "INTERNAL_SERVER_ERROR"
                        }
                        """
                            )
                    )
            )
    })
    ApiResponse<MedicalNewsResponse> getHealthHeadlines(
            @Parameter(
                    description = "국가 코드 (ISO 3166-1 alpha-2, 2자리 소문자)",
                    example = "us"
            )
            @RequestParam(defaultValue = "us")
            @Pattern(regexp = "^[a-z]{2}$", message = "국가 코드는 2자리 소문자여야 합니다 (예: us, kr, jp)")
            String country,

            @Parameter(
                    description = "페이지 번호 (1부터 시작)",
                    example = "1"
            )
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
            @Max(value = 100, message = "페이지는 100 이하여야 합니다")
            int page,

            @Parameter(
                    description = "페이지당 기사 수 (1-100)",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
            @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
            int pageSize
    );
}
