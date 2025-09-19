package com.vitaltrip.vitaltrip.news.service;

import com.vitaltrip.vitaltrip.news.client.NewsApiClient;
import com.vitaltrip.vitaltrip.news.dto.MedicalNewsResponse;
import com.vitaltrip.vitaltrip.news.dto.NewsApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsApiClient newsApiClient;

    /**
     * 의료 관련 뉴스를 검색합니다.
     */
    public MedicalNewsResponse searchMedicalNews(String query, int page, int pageSize, String language) {
        // 의료 관련 키워드가 포함된 검색 쿼리 구성
        String medicalQuery = buildMedicalQuery(query);

        NewsApiResponse apiResponse = newsApiClient.searchMedicalNews(
                medicalQuery, page, pageSize, language, "publishedAt"
        );

        return MedicalNewsResponse.from(apiResponse, page, pageSize);
    }

    /**
     * 건강 관련 최신 헤드라인을 가져옵니다.
     */
    public MedicalNewsResponse getHealthHeadlines(String country, int page, int pageSize) {
        NewsApiResponse apiResponse = newsApiClient.getTopHealthHeadlines(country, page, pageSize);
        return MedicalNewsResponse.from(apiResponse, page, pageSize);
    }

    /**
     * 의료 관련 검색 쿼리를 구성합니다.
     */
    private String buildMedicalQuery(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            // 기본 의료 키워드
            return "health OR medical OR medicine OR healthcare OR hospital OR doctor OR treatment";
        }

        // 사용자 쿼리에 의료 관련 키워드 추가
        return String.format("(%s) AND (health OR medical OR medicine OR healthcare)", userQuery.trim());
    }
}
