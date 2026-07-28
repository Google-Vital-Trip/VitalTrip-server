package com.vitaltrip.vitaltrip.infra.gemini.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GeminiClientConfig {

    private final RestClient.Builder baseRestClientBuilder;
    private final GeminiClientProperties properties;

    @Bean
    @Qualifier("geminiRestClient")
    public RestClient geminiRestClient() {
        return baseRestClientBuilder
                .clone()
                .baseUrl(properties.baseUrl())
                .build();
    }
}
