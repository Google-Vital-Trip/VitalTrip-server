package com.vitaltrip.vitaltrip.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {

    @Value("${gemini.api.base-url}")
    private String geminiBaseUrl;

    @Bean("geminiRestClient")
    public RestClient geminiRestClient() {
        return RestClient.builder()
            .baseUrl(geminiBaseUrl)
            .requestInterceptor(loggingInterceptor())
            .requestInterceptor(userAgentInterceptor())
            .build();
    }

    @Bean("defaultRestClient")
    public RestClient defaultRestClient() {
        return RestClient.builder()
            .requestInterceptor(loggingInterceptor())
            .requestInterceptor(userAgentInterceptor())
            .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("RestClient Request: {} {}", request.getMethod(), request.getURI());

            var response = execution.execute(request, body);

            log.debug("RestClient Response: {} for {} {}",
                response.getStatusCode(), request.getMethod(), request.getURI());

            return response;
        };
    }

    private ClientHttpRequestInterceptor userAgentInterceptor() {
        return (request, body, execution) -> {
            request.getHeaders().add("User-Agent", "VitalTrip/1.0.0 (Spring Boot)");
            return execution.execute(request, body);
        };
    }
}
