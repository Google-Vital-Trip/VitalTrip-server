package com.vitaltrip.vitaltrip.presentation.auth;

import com.vitaltrip.vitaltrip.presentation.auth.docs.AuthControllerDocs;
import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import com.vitaltrip.vitaltrip.presentation.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.application.auth.AuthService;
import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.domain.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final CookieProperties cookieProperties;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ApiResponse<AuthDto.AuthResponse> signUp(@Valid @RequestBody AuthDto.SignUpRequest request) {
        authService.signUp(request);
        return ApiResponse.success("성공");
    }

    @PostMapping("/login")
    @Override
    public ApiResponse<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request, HttpServletResponse response) {
        AuthDto.AuthResponse authResponse = authService.login(request);
        setTokenCookies(response, authResponse.accessToken(), authResponse.refreshToken());
        return ApiResponse.success(authResponse);
    }

    @PostMapping("/admin/login")
    @Override
    public ApiResponse<String> adminLogin(@Valid @RequestBody AuthDto.LoginRequest request, HttpServletResponse response) {
        AuthDto.AuthResponse authResponse = authService.adminLogin(request);
        setTokenCookies(response, authResponse.accessToken(), authResponse.refreshToken());
        return ApiResponse.success("어드민 로그인이 완료되었습니다");
    }

    @PostMapping("/refresh")
    @Override
    public ApiResponse<AuthDto.TokenResponse> refreshToken(@Valid @RequestBody AuthDto.TokenRefreshRequest request, HttpServletResponse response) {
        AuthDto.TokenResponse tokenResponse = authService.refreshToken(request);
        setAccessTokenCookie(response, tokenResponse.accessToken());
        return ApiResponse.success(tokenResponse);
    }

    @PostMapping("/admin/refresh")
    @Override
    public ApiResponse<String> adminRefreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new CustomException(ErrorType.UNAUTHORIZED, "리프레시 토큰이 없습니다");
        }

        AuthDto.TokenRefreshRequest tokenRequest = new AuthDto.TokenRefreshRequest(refreshToken);
        AuthDto.TokenResponse tokenResponse = authService.refreshToken(tokenRequest);
        setAccessTokenCookie(response, tokenResponse.accessToken());

        return ApiResponse.success("토큰이 갱신되었습니다");
    }

    @PutMapping("/password")
    @Override
    public ApiResponse<String> changePassword(@AuthenticationPrincipal User user, @Valid @RequestBody AuthDto.PasswordChangeRequest request) {
        authService.changePassword(user, request);
        return ApiResponse.success("비밀번호가 변경되었습니다");
    }

    @PostMapping("/logout")
    @Override
    public ApiResponse<String> logout(@AuthenticationPrincipal User user, HttpServletResponse response) {
        clearTokenCookies(response);
        return ApiResponse.success("로그아웃되었습니다. 토큰이 삭제되었습니다.");
    }

    @GetMapping("/check-email")
    @Override
    public ApiResponse<AuthDto.EmailCheckResponse> checkEmailAvailability(@RequestParam("email") @Email(message = "유효한 이메일 형식이 아닙니다") @NotBlank(message = "이메일은 필수입니다") String email) {
        AuthDto.EmailCheckResponse response = authService.checkEmailAvailability(email);
        return ApiResponse.success(response);
    }

    private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        setAccessTokenCookie(response, accessToken);
        setRefreshTokenCookie(response, refreshToken);
    }

    private void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        addSecureCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, cookieProperties.accessTokenMaxAge());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        addSecureCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieProperties.refreshTokenMaxAge());
    }

    private void clearTokenCookies(HttpServletResponse response) {
        addSecureCookie(response, ACCESS_TOKEN_COOKIE_NAME, "", 0);
        addSecureCookie(response, REFRESH_TOKEN_COOKIE_NAME, "", 0);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void addSecureCookie(HttpServletResponse response, String name, String value, int maxAge) {
        try {
            ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
                    .httpOnly(true)
                    .secure(cookieProperties.secure())
                    .path("/")
                    .maxAge(maxAge);

            if (cookieProperties.sameSite() != null && !cookieProperties.sameSite().trim().isEmpty()) {
                cookieBuilder.sameSite(cookieProperties.sameSite());
            }

            if (cookieProperties.domain() != null && !cookieProperties.domain().trim().isEmpty()) {
                cookieBuilder.domain(cookieProperties.domain());
            }

            ResponseCookie cookie = cookieBuilder.build();
            response.addHeader("Set-Cookie", cookie.toString());
        } catch (Exception e) {
            StringBuilder cookieValue = new StringBuilder();
            cookieValue.append(name).append("=").append(value);
            cookieValue.append("; Path=/; Max-Age=").append(maxAge).append("; HttpOnly");

            if (cookieProperties.secure()) cookieValue.append("; Secure");
            if (cookieProperties.sameSite() != null)
                cookieValue.append("; SameSite=").append(cookieProperties.sameSite());
            if (cookieProperties.domain() != null && !cookieProperties.domain().trim().isEmpty()) {
                cookieValue.append("; Domain=").append(cookieProperties.domain());
            }

            response.addHeader("Set-Cookie", cookieValue.toString());
        }
    }
}
