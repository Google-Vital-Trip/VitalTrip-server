package com.vitaltrip.vitaltrip.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("VitalTrip API")
                .description("VitalTrip 서비스 API 문서")
                .version("v1.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("로컬 서버"),
                new Server()
                    .url("http://dkswoalstest.duckdns.org")
                    .description("개발 서버")
            ));
    }
}
