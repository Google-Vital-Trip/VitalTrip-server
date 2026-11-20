package com.vitaltrip.vitaltrip.domain.location.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "bigdatacloud.api")
public record BigDataCloudProperties(
        @NotBlank String baseUrl
) {
}
