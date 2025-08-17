package com.vitaltrip.vitaltrip.domain.location.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class GoogleLocationClientConfig {

    @Value("${google.api.location.base-url}")
    private String googleLocationBaseUrl;

    @Value("${google.api.location.api-key}")
    private String googleLocationApiKey;

    @Bean
    @Qualifier("googleLocationRestClient")
    public RestClient googleLocationRestClient(RestClient.Builder baseRestClientBuilder) {
        return baseRestClientBuilder
                .baseUrl(googleLocationBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("X-Goog-Api-Key", googleLocationApiKey)
                .build();
    }
}
