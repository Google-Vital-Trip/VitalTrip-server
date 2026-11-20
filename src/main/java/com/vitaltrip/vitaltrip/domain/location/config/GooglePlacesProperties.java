package com.vitaltrip.vitaltrip.domain.location.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "google.api.places")
public record GooglePlacesProperties(
        String baseUrl,
        @NotBlank String apiKey,
        @Pattern(regexp = "^[a-zA-Z.]+(,[a-zA-Z.]+)*$", message = "field mask must be comma-separated with no whitespace")
        @NotBlank String defaultFieldMask
) {
}
