package com.vitaltrip.vitaltrip.presentation.auth;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth.cookie")
public record CookieProperties(
        String domain,
        boolean secure,
        @Pattern(regexp = "Lax|Strict|None") String sameSite,
        @Positive int accessTokenMaxAge,
        @Positive int refreshTokenMaxAge
) {
}
