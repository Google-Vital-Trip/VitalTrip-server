package com.vitaltrip.vitaltrip.admin.dto;

import com.vitaltrip.vitaltrip.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminUserResponse(
    @Schema(description = "사용자 ID", example = "1")
    Long id,

    @Schema(description = "이메일", example = "user@example.com")
    String email,

    @Schema(description = "이름", example = "홍길동")
    String name,

    @Schema(description = "생년월일", example = "1990-01-01")
    LocalDate birthDate,

    @Schema(description = "국가 코드", example = "KR")
    String countryCode,

    @Schema(description = "전화번호", example = "+821012345678")
    String phoneNumber,

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    String profileImageUrl,

    @Schema(description = "로그인 제공자", example = "LOCAL")
    User.AuthProvider provider,

    @Schema(description = "제공자 ID", example = "google123")
    String providerId,

    @Schema(description = "사용자 역할", example = "USER")
    User.Role role,

    @Schema(description = "계정 생성일", example = "2024-01-01T00:00:00")
    LocalDateTime createdAt,

    @Schema(description = "최종 수정일", example = "2024-01-01T00:00:00")
    LocalDateTime updatedAt
) {
    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getBirthDate(),
            user.getCountryCode(),
            user.getPhoneNumber(),
            user.getProfileImageUrl(),
            user.getProvider(),
            user.getProviderId(),
            user.getRole(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
