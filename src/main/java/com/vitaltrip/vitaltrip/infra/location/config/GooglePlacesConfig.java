package com.vitaltrip.vitaltrip.infra.location.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class GooglePlacesConfig {

    private final GooglePlacesProperties properties;

    @Bean
    @Qualifier("googlePlaceRestClient")
    public RestClient googlePlacesRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Goog-Api-Key", properties.apiKey())
                .defaultHeader("X-Goog-FieldMask", properties.defaultFieldMask())
                .build();
    }

}
