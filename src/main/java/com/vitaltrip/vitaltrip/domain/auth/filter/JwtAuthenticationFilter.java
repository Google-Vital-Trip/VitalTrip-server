package com.vitaltrip.vitaltrip.domain.auth.filter;

import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
            filterChain.doFilter(request, response);
            return;
        }

        String userId = jwtUtil.getUserId(token);
        User user = userRepository.findById(Long.parseLong(userId)).orElse(null);

        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isTempToken(token)) {
            if (isTempTokenAllowedPath(requestURI)) {
                setTempAuthentication(request, user);
            } else {
                filterChain.doFilter(request, response);
                return;
            }
        } else {
            setAuthentication(request, user);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private boolean isTempTokenAllowedPath(String requestURI) {
        return requestURI.equals("/api/oauth2/complete-profile") ||
            requestURI.equals("/api/oauth2/status");
    }

    private void setAuthentication(HttpServletRequest request, User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
            "ROLE_" + user.getRole().name());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            user, null, Collections.singletonList(authority)
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    private void setTempAuthentication(HttpServletRequest request, User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_TEMP_USER");

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            user, null, Collections.singletonList(authority)
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
}
