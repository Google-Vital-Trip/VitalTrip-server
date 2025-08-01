package com.vitaltrip.vitaltrip.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AuthDto {

    public record SignUpRequest(
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
        String name,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, max = 100, message = "비밀번호는 8-100자 사이여야 합니다")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "비밀번호는 소문자, 숫자, 특수문자를 포함해야 합니다")
        String password,

        @NotBlank(message = "비밀번호 확인은 필수입니다")
        String passwordConfirm,

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

        public boolean isPasswordMatched() {
            return password != null && password.equals(passwordConfirm);
        }
    }

    public record LoginRequest(
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
    ) {

    }

    public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserInfo user
    ) {

    }

    public record UserInfo(
        Long id,
        String email,
        String name,
        LocalDate birthDate,
        String countryCode,
        String phoneNumber,
        String profileImageUrl
    ) {

    }

    public record TokenRefreshRequest(
        @NotBlank(message = "리프레시 토큰은 필수입니다")
        String refreshToken
    ) {

    }

    public record TokenResponse(
        String accessToken
    ) {

    }

    public record PasswordChangeRequest(
        @NotBlank(message = "현재 비밀번호는 필수입니다")
        String currentPassword,

        @NotBlank(message = "새 비밀번호는 필수입니다")
        @Size(min = 8, max = 100, message = "비밀번호는 8-100자 사이여야 합니다")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "비밀번호는 소문자, 숫자, 특수문자를 포함해야 합니다")
        String newPassword,

        @NotBlank(message = "새 비밀번호 확인은 필수입니다")
        String newPasswordConfirm
    ) {

        public boolean isNewPasswordMatched() {
            return newPassword != null && newPassword.equals(newPasswordConfirm);
        }
    }

    public record ProfileUpdateRequest(
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

    public record EmailCheckResponse(
        @Schema(description = "이메일 사용 가능 여부", example = "true")
        boolean available
    ) {

    }
}
