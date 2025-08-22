package com.vitaltrip.vitaltrip.auth.filter;

import com.vitaltrip.vitaltrip.auth.util.JwtUtil;
import com.vitaltrip.vitaltrip.user.domain.User;
import com.vitaltrip.vitaltrip.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class FirstAidAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        try {
            if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTempToken(token)) {
                String userId = jwtUtil.getUserId(token);
                User user = userRepository.findById(Long.parseLong(userId)).orElse(null);

                if (user != null) {
                    setAuthenticatedUser(request, user);
                } else {
                    setAnonymousUser(request);
                }
            } else {
                setAnonymousUser(request);
            }
        } catch (Exception e) {
            setAnonymousUser(request);
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

    private void setAuthenticatedUser(HttpServletRequest request, User user) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, Collections.singletonList(authority)
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void setAnonymousUser(HttpServletRequest request) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ANONYMOUS_FIRST_AID");

        AnonymousFirstAidUser anonymousUser = new AnonymousFirstAidUser();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                anonymousUser, null, Collections.singletonList(authority)
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static class AnonymousFirstAidUser {
        public boolean isAnonymous() {
            return true;
        }

        public String getDisplayName() {
            return "Anonymous First Aid User";
        }

        @Override
        public String toString() {
            return "AnonymousFirstAidUser{}";
        }
    }
}
