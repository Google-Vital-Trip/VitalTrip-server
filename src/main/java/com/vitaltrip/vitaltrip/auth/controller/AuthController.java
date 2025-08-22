package com.vitaltrip.vitaltrip.auth.controller;

import com.vitaltrip.vitaltrip.auth.controller.docs.AuthControllerDocs;
import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.auth.service.AuthService;
import com.vitaltrip.vitaltrip.user.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ApiResponse<AuthDto.AuthResponse> signUp(@Valid @RequestBody AuthDto.SignUpRequest request) {
        authService.signUp(request);
        return ApiResponse.success("성공");
    }

    @PostMapping("/login")
    @Override
    public ApiResponse<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        AuthDto.AuthResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/refresh")
    @Override
    public ApiResponse<AuthDto.TokenResponse> refreshToken(@Valid @RequestBody AuthDto.TokenRefreshRequest request) {
        AuthDto.TokenResponse response = authService.refreshToken(request);
        return ApiResponse.success(response);
    }

    @PutMapping("/password")
    @Override
    public ApiResponse<String> changePassword(@AuthenticationPrincipal User user,
                                              @Valid @RequestBody AuthDto.PasswordChangeRequest request) {
        authService.changePassword(user, request);
        return ApiResponse.success("비밀번호가 변경되었습니다");
    }

    @PostMapping("/logout")
    @Override
    public ApiResponse<String> logout(@AuthenticationPrincipal User user) {
        return ApiResponse.success("로그아웃되었습니다. 클라이언트에서 토큰을 삭제해주세요.");
    }

    @GetMapping("/check-email")
    @Override
    public ApiResponse<AuthDto.EmailCheckResponse> checkEmailAvailability(
            @RequestParam("email") @Email(message = "유효한 이메일 형식이 아닙니다") @NotBlank(message = "이메일은 필수입니다") String email) {
        AuthDto.EmailCheckResponse response = authService.checkEmailAvailability(email);

        if (response.available()) {
            return ApiResponse.success(response);
        } else {
            return ApiResponse.success(response);
        }
    }
}
