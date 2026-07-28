package com.vitaltrip.vitaltrip.infra.news.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class NewsApiConfig {

    private final NewsApiProperties properties;

    @Bean
    @Qualifier("newsApiRestClient")
    public RestClient newsApiRestClient() {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, "VitalTrip-API/1.0.0")
                .build();
    }
}
