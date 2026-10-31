package com.vitaltrip.vitaltrip.infra.guide.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "guide")
public record GuideProperties(
        @NotBlank String location,
        @NotEmpty List<@NotBlank String> languages
) {
}
