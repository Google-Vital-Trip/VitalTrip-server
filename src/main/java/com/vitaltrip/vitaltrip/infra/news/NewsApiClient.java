package com.vitaltrip.vitaltrip.infra.news;

import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.presentation.news.dto.NewsApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsApiClient {

    private final RestClient newsApiRestClient;

    @Value("${news.api.key}")
    private String apiKey;

    /**
     * 의료 관련 뉴스를 검색합니다.
     */
    public NewsApiResponse searchMedicalNews(String query, int page, int pageSize, String language, String sortBy) {
        log.info("Searching medical news: query={}, page={}, pageSize={}, language={}, sortBy={}",
                query, page, pageSize, language, sortBy);

        try {
            NewsApiResponse response = newsApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/everything")
                            .queryParam("q", query)
                            .queryParam("page", page)
                            .queryParam("pageSize", pageSize)
                            .queryParam("language", language)
                            .queryParam("sortBy", sortBy)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .body(NewsApiResponse.class);

            if (response == null) {
                throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR, "News API 응답이 null입니다");
            }

            log.info("Medical news search completed: {} articles found",
                    response.articles() != null ? response.articles().size() : 0);

            return response;

        } catch (RestClientException e) {
            log.error("News API 호출 실패: {}", e.getMessage());
            // 더 자세한 에러 정보 로깅
            if (e.getMessage().contains("400")) {
                log.error("News API 400 에러 - API 키나 파라미터 확인 필요");
            }
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                    "News API 호출 실패. API 키나 파라미터를 확인해주세요.");
        } catch (Exception e) {
            log.error("뉴스 검색 중 예상치 못한 오류", e);
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR, "뉴스 검색 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 카테고리의 최신 헤드라인을 가져옵니다.
     */
    public NewsApiResponse getTopHealthHeadlines(String country, int page, int pageSize) {
        log.info("Getting top health headlines: country={}, page={}, pageSize={}", country, page, pageSize);

        try {
            NewsApiResponse response = newsApiRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/top-headlines")
                            .queryParam("category", "health")
                            .queryParam("country", country)
                            .queryParam("page", page)
                            .queryParam("pageSize", pageSize)
                            .queryParam("apiKey", apiKey)
                            .build())
                    .retrieve()
                    .body(NewsApiResponse.class);

            if (response == null) {
                throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR, "News API 응답이 null입니다");
            }

            log.info("Top health headlines retrieved: {} articles found",
                    response.articles() != null ? response.articles().size() : 0);

            return response;

        } catch (RestClientException e) {
            log.error("News API 헤드라인 호출 실패: {}", e.getMessage());
            if (e.getMessage().contains("400")) {
                log.error("News API 400 에러 - API 키나 국가 코드 확인 필요");
            }
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR,
                    "News API 헤드라인 조회 실패. API 키나 국가 코드를 확인해주세요.");
        } catch (Exception e) {
            log.error("헤드라인 조회 중 예상치 못한 오류", e);
            throw new CustomException(ErrorType.INTERNAL_SERVER_ERROR, "헤드라인 조회 실패: " + e.getMessage());
        }
    }
}
