package com.vitaltrip.vitaltrip.domain.auth.service;

import com.vitaltrip.vitaltrip.common.exception.CustomException;
import com.vitaltrip.vitaltrip.common.exception.ErrorType;
import com.vitaltrip.vitaltrip.domain.auth.dto.OAuthDto;
import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public OAuthDto.CompleteProfileResponse completeProfile(String tempToken,
        OAuthDto.CompleteProfileRequest request) {

        String userId = jwtUtil.getUserId(tempToken);
        User user = userRepository.findById(Long.parseLong(userId))
            .orElseThrow(() -> new CustomException(ErrorType.USER_NOT_FOUND));

        if (user.getProvider() == User.AuthProvider.LOCAL) {
            throw new CustomException(ErrorType.INVALID_REQUEST, "일반 회원가입 사용자는 이 기능을 사용할 수 없습니다.");
        }

        user.updateProfile(
            request.name(),
            request.birthDate(),
            request.countryCode(),
            request.phoneNumber()
        );

        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);

        OAuthDto.OAuthUserInfo userInfo = new OAuthDto.OAuthUserInfo(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getName(),
            savedUser.getBirthDate(),
            savedUser.getCountryCode(),
            savedUser.getPhoneNumber(),
            savedUser.getProfileImageUrl(),
            savedUser.getProvider().name()
        );

        return new OAuthDto.CompleteProfileResponse(accessToken, refreshToken, userInfo);
    }

}
