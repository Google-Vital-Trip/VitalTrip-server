package com.vitaltrip.vitaltrip.infra.gemini.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ai.gemini.router")
public record GeminiRouterProperties(
        @NotBlank String model,
        @NotNull Duration readTimeout,
        String thinkingLevel
) {
}
