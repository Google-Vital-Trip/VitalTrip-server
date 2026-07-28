package com.vitaltrip.vitaltrip.presentation.news;

import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import com.vitaltrip.vitaltrip.presentation.news.docs.NewsControllerDocs;
import com.vitaltrip.vitaltrip.presentation.news.dto.MedicalNewsResponse;
import com.vitaltrip.vitaltrip.application.news.NewsService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController implements NewsControllerDocs {

    private final NewsService newsService;

    @GetMapping("/medical")
    @Override
    public ApiResponse<MedicalNewsResponse> getMedicalNews(
            @RequestParam(required = false) String query,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
            @Max(value = 100, message = "페이지는 100 이하여야 합니다")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
            @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
            int pageSize,

            @RequestParam(defaultValue = "en")
            @Pattern(regexp = "^(en|ko|ja|zh|es|fr|de|ru|ar)$",
                    message = "지원되는 언어: en, ko, ja, zh, es, fr, de, ru, ar")
            String language
    ) {
        MedicalNewsResponse response = newsService.searchMedicalNews(query, page, pageSize, language);
        return ApiResponse.success(response);
    }

    @GetMapping("/headlines")
    @Override
    public ApiResponse<MedicalNewsResponse> getHealthHeadlines(
            @RequestParam(defaultValue = "us")
            @Pattern(regexp = "^[a-z]{2}$", message = "국가 코드는 2자리 소문자여야 합니다 (예: us, kr, jp)")
            String country,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "페이지는 1 이상이어야 합니다")
            @Max(value = 100, message = "페이지는 100 이하여야 합니다")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
            @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
            int pageSize
    ) {
        MedicalNewsResponse response = newsService.getHealthHeadlines(country, page, pageSize);
        return ApiResponse.success(response);
    }
}
