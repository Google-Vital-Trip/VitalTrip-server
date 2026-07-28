package com.vitaltrip.vitaltrip.infra.gemini.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ai.gemini")
public record GeminiClientProperties(
        @NotBlank String apiKey,
        @NotBlank String baseUrl,
        @NotBlank String model
) {
}
