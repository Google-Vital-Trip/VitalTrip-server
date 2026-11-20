package com.vitaltrip.vitaltrip.global.security.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(
        @NotEmpty
        List<@Pattern(
                regexp = "^https?://[A-Za-z0-9.-]+(:\\d{1,5})?$",
                message = "CORS allowed-origins는 와일드카드나 경로 없이 scheme://host[:port] 형태여야 합니다"
        ) String> allowedOrigins
) {
}
