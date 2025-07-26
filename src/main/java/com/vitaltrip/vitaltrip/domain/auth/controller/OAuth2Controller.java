package com.vitaltrip.vitaltrip.domain.auth.controller;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.auth.dto.OAuthDto;
import com.vitaltrip.vitaltrip.domain.auth.service.OAuth2Service;
import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;
    private final JwtUtil jwtUtil;

    @PostMapping("/complete-profile")
    public ApiResponse<OAuthDto.CompleteProfileResponse> completeProfile(
        HttpServletRequest request,
        @Valid @RequestBody OAuthDto.CompleteProfileRequest profileRequest) {

        String tempToken = extractTempToken(request);

        if (!jwtUtil.validateToken(tempToken) || !jwtUtil.isTempToken(tempToken)) {
            return ApiResponse.error("유효하지 않은 임시 토큰입니다.", "INVALID_TEMP_TOKEN");
        }

        OAuthDto.CompleteProfileResponse response = oauth2Service.completeProfile(tempToken,
            profileRequest);
        return ApiResponse.success(response);
    }

    @GetMapping("/login-url")
    public ApiResponse<Object> getOAuth2LoginUrl(HttpServletRequest request) {
        String baseUrl =
            request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        return ApiResponse.success(new Object() {
            public String getGoogleLoginUrl() {
                return baseUrl + "/oauth2/authorization/google";
            }

            public String getMessage() {
                return "위 URL로 이동하면 Google OAuth2 로그인이 시작됩니다.";
            }
        });
    }

    private String extractTempToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        throw new IllegalArgumentException("Authorization 헤더에서 토큰을 찾을 수 없습니다.");
    }
}
