package com.vitaltrip.vitaltrip.infra.location.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class BigDataCloudConfig {

    @Bean
    @Qualifier("bigDataCloudRestClient")
    public RestClient bigDataCloudRestClient(RestClient.Builder baseRestClientBuilder, BigDataCloudProperties properties) {
        return baseRestClientBuilder
                .baseUrl(properties.baseUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
