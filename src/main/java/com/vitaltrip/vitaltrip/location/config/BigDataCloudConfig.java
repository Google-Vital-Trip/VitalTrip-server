package com.vitaltrip.vitaltrip.location.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class BigDataCloudConfig {

    private static final String BIG_DATA_CLOUD_BASE_URL = "https://api.bigdatacloud.net";

    @Bean
    @Qualifier("bigDataCloudRestClient")
    public RestClient bigDataCloudRestClient(RestClient.Builder baseRestClientBuilder) {
        return baseRestClientBuilder
            .baseUrl(BIG_DATA_CLOUD_BASE_URL)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
}
