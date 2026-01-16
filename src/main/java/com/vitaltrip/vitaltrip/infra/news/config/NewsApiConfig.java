package com.vitaltrip.vitaltrip.infra.news.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class NewsApiConfig {

    private static final String NEWS_API_BASE_URL = "https://newsapi.org";

    @Bean
    @Qualifier("newsApiRestClient")
    public RestClient newsApiRestClient() {
        return RestClient.builder()
                .baseUrl(NEWS_API_BASE_URL)
                .defaultHeader("User-Agent", "VitalTrip-API/1.0.0")
                .build();
    }
}
