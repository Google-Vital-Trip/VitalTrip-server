package com.vitaltrip.vitaltrip.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final ClientHttpRequestInterceptor loggingInterceptor;
    private final ClientHttpRequestInterceptor userAgentInterceptor;
    private final ClientHttpRequestInterceptor errorHandlingInterceptor;

    @Bean
    public RestClient.Builder baseRestClientBuilder() {
        return RestClient.builder()
                .requestInterceptor(userAgentInterceptor)
                .requestInterceptor(loggingInterceptor)
                .requestInterceptor(errorHandlingInterceptor);
    }

    @Bean
    public RestClient defaultRestClient(RestClient.Builder baseRestClientBuilder) {
        return baseRestClientBuilder.build();
    }

}
