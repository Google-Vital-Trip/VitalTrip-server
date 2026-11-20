package com.vitaltrip.vitaltrip.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class OAuthDto {

    public record GoogleUserInfo(
        String email,
        String name,
        String profileImageUrl,
        String providerId
    ) {

    }

    public record CompleteProfileRequest(
        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
        String name,

        @NotNull(message = "생년월일은 필수입니다")
        @Past(message = "생년월일은 과거 날짜여야 합니다")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,

        @NotBlank(message = "국가 코드는 필수입니다")
        @Pattern(regexp = "^[A-Z]{2}$", message = "국가 코드는 2자리 대문자여야 합니다 (예: KR, US)")
        String countryCode,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "올바른 전화번호 형식이 아닙니다 (예: +821012345678)")
        String phoneNumber
    ) {

    }

    public record CompleteProfileResponse(
        String accessToken,
        String refreshToken,
        OAuthUserInfo user
    ) {

    }

    public record OAuthUserInfo(
        Long id,
        String email,
        String name,
        LocalDate birthDate,
        String countryCode,
        String phoneNumber,
        String profileImageUrl,
        String provider  // "GOOGLE", "KAKAO" 등
    ) {

    }

    public record TempTokenResponse(
        String tempToken,
        String email,
        String name,
        String profileImageUrl,
        boolean needsProfile
    ) {

    }
}
