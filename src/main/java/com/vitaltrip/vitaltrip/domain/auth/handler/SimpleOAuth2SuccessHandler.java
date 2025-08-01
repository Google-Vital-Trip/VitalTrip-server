package com.vitaltrip.vitaltrip.domain.auth.handler;

import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimpleOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String frontendRedirectUri;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        if (response.isCommitted()) {
            return;
        }

        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String picture = oauth2User.getAttribute("picture");
            String sub = oauth2User.getAttribute("sub");

            if (email == null || name == null) {
                redirectWithError(response, "OAUTH_ERROR", "사용자 정보를 가져올 수 없습니다");
                return;
            }

            User user = processOAuth2User(email, name, picture, sub);

            if (isProfileComplete(user)) {
                String accessToken = jwtUtil.generateAccessToken(user);
                String refreshToken = jwtUtil.generateRefreshToken(user);

                redirectWithTokens(response, accessToken, refreshToken);
            } else {
                String tempToken = jwtUtil.generateTempToken(user);

                redirectWithTempToken(response, tempToken, user.getEmail(), user.getName(),
                    user.getProfileImageUrl());
            }

        } catch (Exception e) {
            log.error("OAuth2 authentication error", e);
            redirectWithError(response, "INTERNAL_ERROR", "인증 처리 중 오류가 발생했습니다");
        }
    }

    private User processOAuth2User(String email, String name, String picture, String sub) {
        return userRepository.findByEmail(email)
            .map(existingUser -> {
                if (picture != null && !picture.equals(existingUser.getProfileImageUrl())) {
                    existingUser.updateProfileImage(picture);
                    return userRepository.save(existingUser);
                }
                return existingUser;
            })
            .orElseGet(() -> {
                User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .profileImageUrl(picture)
                    .provider(User.AuthProvider.GOOGLE)
                    .providerId(sub)
                    .role(User.Role.USER)
                    .build();

                return userRepository.save(newUser);
            });
    }

    private boolean isProfileComplete(User user) {
        return user.getBirthDate() != null &&
            user.getCountryCode() != null &&
            user.getCountryCode().length() == 2;
    }

    private void redirectWithTokens(HttpServletResponse response, String accessToken,
        String refreshToken)
        throws IOException {

        String redirectUrl = frontendRedirectUri +
            "?success=true" +
            "&accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8) +
            "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }

    private void redirectWithTempToken(HttpServletResponse response, String tempToken,
        String email, String name, String profileImageUrl) throws IOException {

        StringBuilder redirectUrl = new StringBuilder(frontendRedirectUri)
            .append("?needsProfile=true")
            .append("&tempToken=").append(URLEncoder.encode(tempToken, StandardCharsets.UTF_8))
            .append("&email=").append(URLEncoder.encode(email, StandardCharsets.UTF_8))
            .append("&name=").append(URLEncoder.encode(name, StandardCharsets.UTF_8));

        if (profileImageUrl != null) {
            redirectUrl.append("&profileImageUrl=")
                .append(URLEncoder.encode(profileImageUrl, StandardCharsets.UTF_8));
        }

        response.sendRedirect(redirectUrl.toString());
    }

    private void redirectWithError(HttpServletResponse response, String errorCode, String message)
        throws IOException {

        String redirectUrl = frontendRedirectUri +
            "?error=true" +
            "&errorCode=" + URLEncoder.encode(errorCode, StandardCharsets.UTF_8) +
            "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }
}
