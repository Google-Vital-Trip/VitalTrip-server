package com.vitaltrip.vitaltrip.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.auth.dto.OAuthDto;
import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Value("${app.oauth2.authorized-redirect-uri:TEMP}")
    private String authorizedRedirectUri;

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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "사용자 정보를 가져올 수 없습니다.");
                return;
            }

            User user = processOAuth2User(email, name, picture, sub);

            //프론트 리다이랙션 주소가 정해질때까지 임시 메서드
            handleJsonResponse(response, user);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "인증 처리 중 오류가 발생했습니다.");
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

    //프론트 리다이랙션 주소가 정해질때까지 임시 메서드
    private void handleJsonResponse(HttpServletResponse response, User user) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_OK);

        if (isProfileComplete(user)) {
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            OAuthDto.OAuthUserInfo userInfo = new OAuthDto.OAuthUserInfo(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getBirthDate(),
                user.getCountryCode(),
                user.getPhoneNumber(),
                user.getProfileImageUrl(),
                user.getProvider().name()
            );

            OAuthDto.CompleteProfileResponse successResponse = new OAuthDto.CompleteProfileResponse(
                accessToken,
                refreshToken,
                userInfo
            );

            ApiResponse<OAuthDto.CompleteProfileResponse> apiResponse = ApiResponse.success(
                successResponse);
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        } else {
            String tempToken = jwtUtil.generateTempToken(user);

            OAuthDto.TempTokenResponse tempResponse = new OAuthDto.TempTokenResponse(
                tempToken,
                user.getEmail(),
                user.getName(),
                user.getProfileImageUrl(),
                true
            );

            ApiResponse<OAuthDto.TempTokenResponse> apiResponse = ApiResponse.success(tempResponse);
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        }

    }

    private boolean isProfileComplete(User user) {
        return user.getBirthDate() != null &&
            user.getCountryCode() != null &&
            user.getCountryCode().length() == 2;
    }
}
