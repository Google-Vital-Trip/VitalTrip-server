package com.vitaltrip.vitaltrip.global.security.config;

import com.vitaltrip.vitaltrip.global.security.filter.FirstAidAuthenticationFilter;
import com.vitaltrip.vitaltrip.global.security.filter.JwtAuthenticationFilter;
import com.vitaltrip.vitaltrip.domain.auth.handler.SimpleOAuth2SuccessHandler;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
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

    private final FirstAidAuthenticationFilter firstAidAuthenticationFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SimpleOAuth2SuccessHandler oAuth2SuccessHandler;
    private final CorsProperties corsProperties;

    @Bean
    @Order(0)
    @Profile("loadtest")
    public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain firstAidFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/api/first-aid/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .addFilterBefore(firstAidAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .headers(headers -> headers
                        .frameOptions(FrameOptionsConfig::sameOrigin)
                )
                .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(request -> !request.getRequestURI().startsWith("/api/first-aid"))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 기본 헬스체크 및 정적 리소스
                        .requestMatchers("/", "/home", "/health", "/actuator/health").permitAll()

                        // 인증 관련 퍼블릭 엔드포인트
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/check-email",
                                "/api/auth/admin/login"
                        ).permitAll()

                        // OAuth2 관련 퍼블릭 엔드포인트
                        .requestMatchers("/api/oauth2/**").permitAll()
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/login/oauth2/code/**",
                                "/login/oauth2/code/google",
                                "/login",
                                "/login/**"
                        ).permitAll()

                        // 개발/문서 관련 퍼블릭 엔드포인트
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // 퍼블릭 API
                        .requestMatchers(
                                "/api/location/**",
                                "/api/news/**",
                                "/api/v2/first-aid/**"
                        ).permitAll()

                        // 관리자 엔드포인트
                        .requestMatchers("/api/admin/me").authenticated()
                        .requestMatchers("/api/admin/users").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 기타 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
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

        configuration.setAllowedOrigins(corsProperties.allowedOrigins());

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "Accept", "Accept-Language", "X-Requested-With"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
