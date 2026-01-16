package com.vitaltrip.vitaltrip.presentation.filter;

import com.vitaltrip.vitaltrip.infra.jwt.JwtUtil;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        String token = getTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            log.debug("유효하지 않은 JWT 토큰: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String userId = jwtUtil.getUserId(token);
        User user = userRepository.findById(Long.parseLong(userId)).orElse(null);

        if (user == null) {
            log.debug("사용자를 찾을 수 없음: userId={}", userId);
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isTempToken(token)) {
            if (isTempTokenAllowedPath(requestURI)) {
                setTempAuthentication(request, user);
            } else {
                log.debug("임시 토큰으로 허용되지 않은 경로 접근: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            setAuthentication(request, user);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 요청에서 JWT 토큰을 추출합니다.
     * 우선순위: 1) Authorization 헤더, 2) accessToken 쿠키
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. Authorization 헤더에서 토큰 추출
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            String headerToken = bearerToken.substring(BEARER_PREFIX.length());
            log.debug("헤더에서 토큰 추출: {}", maskToken(headerToken));
            return headerToken;
        }

        // 2. 쿠키에서 토큰 추출
        String cookieToken = getTokenFromCookie(request);
        if (StringUtils.hasText(cookieToken)) {
            log.debug("쿠키에서 토큰 추출: {}", maskToken(cookieToken));
            return cookieToken;
        }

        return null;
    }

    /**
     * 쿠키에서 액세스 토큰을 추출합니다.
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        for (Cookie cookie : request.getCookies()) {
            if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                String tokenValue = cookie.getValue();
                if (StringUtils.hasText(tokenValue)) {
                    return tokenValue;
                }
            }
        }

        return null;
    }

    /**
     * 임시 토큰이 허용되는 경로인지 확인합니다.
     */
    private boolean isTempTokenAllowedPath(String requestURI) {
        return requestURI.equals("/api/oauth2/complete-profile") ||
                requestURI.equals("/api/oauth2/status");
    }

    /**
     * 일반 사용자 인증을 설정합니다.
     */
    private void setAuthentication(HttpServletRequest request, User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().name());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, Collections.singletonList(authority)
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("사용자 인증 설정 완료: userId={}, role={}", user.getId(), user.getRole());
    }

    /**
     * 임시 토큰 사용자 인증을 설정합니다.
     */
    private void setTempAuthentication(HttpServletRequest request, User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_TEMP_USER");

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, Collections.singletonList(authority)
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("임시 토큰 인증 설정 완료: userId={}", user.getId());
    }

    /**
     * 로그용 토큰 마스킹 (보안을 위해 일부만 표시)
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "...***";
    }
}
