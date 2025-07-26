package com.vitaltrip.vitaltrip.config;

import com.vitaltrip.vitaltrip.domain.auth.filter.JwtAuthenticationFilter;
import com.vitaltrip.vitaltrip.domain.auth.handler.SimpleOAuth2SuccessHandler;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SimpleOAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    // 기본 인증 API
                    "/api/auth/signup",
                    "/api/auth/login",
                    "/api/auth/refresh",

                    // OAuth2 API
                    "/api/oauth2/**",

                    // OAuth2 관련 - 더 구체적으로 허용
                    "/oauth2/**",
                    "/login/oauth2/**",
                    "/login/oauth2/code/**",
                    "/login/oauth2/code/google",
                    "/login",
                    "/login/**",

                    // 개발/문서 관련
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN))
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .headers(headers -> headers
                .frameOptions(FrameOptionsConfig::sameOrigin)
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 모든 오리진 허용 (개발용) - 운영에서는 구체적인 도메인으로 제한
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Swagger UI에서 사용하는 오리진 명시적 추가
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8080",
            "http://dkswoalstest.duckdns.org",
            "https://dkswoalstest.duckdns.org",
            "https://vitaltrip.vercel.app"
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        // Preflight 요청 처리
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", "accept", "Origin",
            "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
