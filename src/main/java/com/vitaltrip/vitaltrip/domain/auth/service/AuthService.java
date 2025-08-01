package com.vitaltrip.vitaltrip.domain.auth.service;

import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.domain.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(AuthDto.SignUpRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorType.DUPLICATE_EMAIL);
        }

        if (!request.isPasswordMatched()) {
            throw new CustomException(ErrorType.INVALID_REQUEST, "비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
            .email(request.email())
            .name(request.name())
            .passwordHash(encodedPassword)
            .birthDate(request.birthDate())
            .countryCode(request.countryCode())
            .phoneNumber(request.phoneNumber())
            .provider(User.AuthProvider.LOCAL)
            .role(User.Role.USER)
            .build();

        userRepository.save(user);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new CustomException(ErrorType.RESOURCE_NOT_FOUND, "등록되지 않은 이메일입니다"));

        if (user.getProvider() != User.AuthProvider.LOCAL) {
            throw new CustomException(ErrorType.INVALID_REQUEST, "소셜 로그인 사용자는 해당 방식으로 로그인해주세요");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new CustomException(ErrorType.UNAUTHORIZED, "비밀번호가 일치하지 않습니다");
        }

        return createAuthResponse(user);
    }

    public AuthDto.TokenResponse refreshToken(AuthDto.TokenRefreshRequest request) {

        String refreshToken = request.refreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(ErrorType.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다");
        }

        String userId = jwtUtil.getUserId(refreshToken);
        User user = userRepository.findById(Long.parseLong(userId))
            .orElseThrow(() -> new CustomException(ErrorType.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다"));

        String newAccessToken = jwtUtil.generateAccessToken(user);

        return new AuthDto.TokenResponse(newAccessToken);
    }

    @Transactional
    public void changePassword(User user, AuthDto.PasswordChangeRequest request) {

        if (user.getProvider() != User.AuthProvider.LOCAL) {
            throw new CustomException(ErrorType.INVALID_REQUEST, "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다");
        }

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorType.UNAUTHORIZED, "현재 비밀번호가 일치하지 않습니다");
        }

        if (!request.isNewPasswordMatched()) {
            throw new CustomException(ErrorType.INVALID_REQUEST, "새 비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }

        String encodedNewPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedNewPassword);

    }

    @Transactional
    public void updateProfile(User user, AuthDto.ProfileUpdateRequest request) {

        user.updateProfile(
            request.name(),
            request.birthDate(),
            request.countryCode(),
            request.phoneNumber()
        );

    }

    public AuthDto.EmailCheckResponse checkEmailAvailability(String email) {
        boolean isAvailable = !userRepository.existsByEmail(email);
        return new AuthDto.EmailCheckResponse(isAvailable);
    }

    private AuthDto.AuthResponse createAuthResponse(User user) {

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        AuthDto.UserInfo userInfo = new AuthDto.UserInfo(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getBirthDate(),
            user.getCountryCode(),
            user.getPhoneNumber(),
            user.getProfileImageUrl()
        );

        return new AuthDto.AuthResponse(accessToken, refreshToken, userInfo);
    }
}
