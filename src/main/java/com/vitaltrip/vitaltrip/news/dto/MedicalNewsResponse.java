package com.vitaltrip.vitaltrip.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record MedicalNewsResponse(
        @Schema(description = "뉴스 기사 목록")
        List<NewsItem> articles,

        @Schema(description = "총 기사 수", example = "100")
        int totalResults,

        @Schema(description = "현재 페이지", example = "1")
        int page,

        @Schema(description = "페이지 크기", example = "10")
        int pageSize
) {

    public record NewsItem(
            @Schema(description = "기사 제목", example = "새로운 의료 기술 개발")
            String title,

            @Schema(description = "기사 설명", example = "혁신적인 의료 기술이 개발되었습니다...")
            String description,

            @Schema(description = "기사 URL", example = "https://example.com/news/1")
            String url,

            @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
            String imageUrl,

            @Schema(description = "출처명", example = "의료신문")
            String sourceName,

            @Schema(description = "발행 일시", example = "2024-01-15T10:30:00")
            LocalDateTime publishedAt
    ) {
    }

    public static MedicalNewsResponse from(NewsApiResponse apiResponse, int page, int pageSize) {
        List<NewsItem> newsItems = apiResponse.articles().stream()
                .map(article -> new NewsItem(
                        article.title(),
                        article.description(),
                        article.url(),
                        article.urlToImage(),
                        article.source() != null ? article.source().name() : "Unknown",
                        parsePublishedAt(article.publishedAt())
                ))
                .toList();

        return new MedicalNewsResponse(
                newsItems,
                apiResponse.totalResults(),
                page,
                pageSize
        );
    }

    private static LocalDateTime parsePublishedAt(String publishedAt) {
        if (publishedAt == null) {
            return LocalDateTime.now();
        }
        try {
            // ISO 8601 형식: "2024-01-15T10:30:00Z"
            if (publishedAt.endsWith("Z")) {
                publishedAt = publishedAt.substring(0, publishedAt.length() - 1);
            }
            return LocalDateTime.parse(publishedAt);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
