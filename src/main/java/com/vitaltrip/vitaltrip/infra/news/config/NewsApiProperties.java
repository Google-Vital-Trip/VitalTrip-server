package com.vitaltrip.vitaltrip.infra.news.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "news.api")
public record NewsApiProperties(
        @NotBlank String baseUrl,
        @NotBlank String apiKey
) {
}
