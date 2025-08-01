package com.vitaltrip.vitaltrip.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                .description("""
                    # VitalTrip 서비스 API 문서
                    
                    여행 관련 서비스 VitalTrip의 RESTful API 문서입니다.
                    
                    ## 인증 방법
                    
                    ### 1. 회원가입/로그인
                    - `/api/auth/signup` 또는 `/api/auth/login` 엔드포인트 사용
                    - 응답으로 받은 `accessToken`을 헤더에 포함하여 인증된 API 호출
                    
                    ### 2. Bearer Token 인증
                    ```
                    Authorization: Bearer {accessToken}
                    ```
                    
                    ### 3. 토큰 갱신
                    - Access Token 만료 시 `/api/auth/refresh` 엔드포인트 사용
                    - Refresh Token으로 새로운 Access Token 발급
                    
                    ## 응답 형식
                    
                    모든 API 응답은 다음과 같은 공통 형식을 사용합니다:
                    
                    ```json
                    {
                      "message": "성공 또는 오류 메시지",
                      "data": "응답 데이터 (성공 시에만 포함)",
                      "errorCode": "오류 코드 (오류 시에만 포함)"
                    }
                    ```
                    
                    ## 오류 코드
                    
                    - `INVALID_REQUEST`: 잘못된 요청
                    - `VALIDATION_FAILED`: 입력값 검증 실패
                    - `UNAUTHORIZED`: 인증 실패
                    - `FORBIDDEN`: 권한 부족
                    - `RESOURCE_NOT_FOUND`: 리소스를 찾을 수 없음
                    - `DUPLICATE_RESOURCE`: 중복된 리소스
                    - `INTERNAL_SERVER_ERROR`: 서버 내부 오류
                    """)
                .version("v1.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("로컬 개발 서버"),
                new Server()
                    .url("http://dkswoalstest.duckdns.org")
                    .description("개발 환경 서버"),
                new Server()
                    .url("https://dkswoalstest.duckdns.org")
                    .description("개발 환경 서버(https)")
            ))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT 액세스 토큰을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다.")
                )
            );
    }
}
