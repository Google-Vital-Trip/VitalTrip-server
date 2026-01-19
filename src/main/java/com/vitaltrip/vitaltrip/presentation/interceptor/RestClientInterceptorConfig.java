package com.vitaltrip.vitaltrip.presentation.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

@Slf4j
@Configuration
public class RestClientInterceptorConfig {

    @Bean
    public ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("RestClient Request: {} {}", request.getMethod(), request.getURI());

            var response = execution.execute(request, body);

            log.debug("RestClient Response: {} for {} {}",
                    response.getStatusCode(), request.getMethod(), request.getURI());

            return response;
        };
    }

    @Bean
    public ClientHttpRequestInterceptor userAgentInterceptor() {
        return (request, body, execution) -> {
            request.getHeaders().add("User-Agent", "VitalTrip/1.0.0 (Spring Boot)");
            return execution.execute(request, body);
        };
    }

    @Bean
    public ClientHttpRequestInterceptor errorHandlingInterceptor() {
        return (request, body, execution) -> {
            try {
                var response = execution.execute(request, body);
                if (response.getStatusCode().isError()) {
                    log.warn("HTTP Error Response: {} for {} {}",
                            response.getStatusCode(), request.getMethod(), request.getURI());
                }
                return response;
            } catch (Exception e) {
                log.error("RestClient Error: {} {}", request.getMethod(), request.getURI(), e);
                throw e;
            }
        };
    }
}
