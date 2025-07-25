package com.vitaltrip.vitaltrip.domain.auth.controller;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.domain.auth.service.AuthService;
import com.vitaltrip.vitaltrip.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthDto.AuthResponse> signUp(
        @Valid @RequestBody AuthDto.SignUpRequest request) {
        AuthDto.AuthResponse response = authService.signUp(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    public ApiResponse<AuthDto.AuthResponse> login(
        @Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.AuthResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthDto.TokenResponse> refreshToken(
        @Valid @RequestBody AuthDto.TokenRefreshRequest request) {
        AuthDto.TokenResponse response = authService.refreshToken(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    public ApiResponse<AuthDto.UserInfo> getMyInfo(@AuthenticationPrincipal User user) {
        AuthDto.UserInfo userInfo = new AuthDto.UserInfo(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getBirthDate(),
            user.getCountryCode(),
            user.getPhoneNumber(),
            user.getProfileImageUrl()
        );

        return ApiResponse.success(userInfo);
    }

    @PutMapping("/profile")
    public ApiResponse<String> updateProfile(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody AuthDto.ProfileUpdateRequest request) {

        authService.updateProfile(user, request);
        return ApiResponse.success("프로필이 업데이트되었습니다");
    }

    @PutMapping("/password")
    public ApiResponse<String> changePassword(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody AuthDto.PasswordChangeRequest request) {

        authService.changePassword(user, request);
        return ApiResponse.success("비밀번호가 변경되었습니다");
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@AuthenticationPrincipal User user) {
        return ApiResponse.success("로그아웃되었습니다. 클라이언트에서 토큰을 삭제해주세요.");
    }
}
