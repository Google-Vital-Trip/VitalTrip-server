package com.vitaltrip.vitaltrip.presentation.auth.docs;

import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import com.vitaltrip.vitaltrip.presentation.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증/인가", description = "회원가입, 로그인, 토큰 관리, 프로필 관리 API")
public interface AuthControllerDocs {

    @Operation(summary = "회원가입", description = "새로운 사용자 계정을 생성합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "회원가입 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "성공"}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "유효성 검증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "비밀번호와 비밀번호 확인이 일치하지 않습니다", "errorCode": "INVALID_REQUEST"}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", description = "이메일 중복",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "이미 사용 중인 이메일입니다.", "errorCode": "DUPLICATE_EMAIL"}""")))
    })
    ApiResponse<AuthDto.AuthResponse> signUp(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원가입 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "email": "test@test.com",
                          "name": "홍길동",
                          "password": "Password123!",
                          "passwordConfirm": "Password123!",
                          "birthDate": "1990-05-15",
                          "countryCode": "KR",
                          "phoneNumber": "+821012345678"
                        }
                        """
                )
            )
        )
        AuthDto.SignUpRequest request);

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다. 토큰은 응답 바디와 쿠키(SameSite=None, Secure)에 모두 설정됩니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "로그인 성공 - 토큰이 응답 바디와 쿠키에 설정됨",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "성공", "data": {"accessToken": "eyJhbGciOiJIUzI1NiJ9...", "refreshToken": "eyJhbGciOiJIUzI1NiJ9...", "user": {"id": 1, "email": "test@test.com", "name": "홍길동"}}}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "비밀번호 불일치",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "비밀번호가 일치하지 않습니다", "errorCode": "UNAUTHORIZED"}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "등록되지 않은 이메일",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "등록되지 않은 이메일입니다", "errorCode": "RESOURCE_NOT_FOUND"}""")))
    })
    ApiResponse<AuthDto.AuthResponse> login(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "로그인 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "email": "test@test.com",
                          "password": "Password123!"
                        }
                        """
                )
            )
        )
        AuthDto.LoginRequest request, @Parameter(hidden = true) HttpServletResponse response);

    @Operation(summary = "어드민 로그인", description = "관리자 계정으로 로그인합니다. 토큰은 쿠키로만 설정됩니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "어드민 로그인 성공 - 토큰이 쿠키로만 설정됨",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "어드민 로그인이 완료되었습니다"}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "ADMIN 권한 없음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "관리자 권한이 필요합니다", "errorCode": "FORBIDDEN"}""")))
    })
    ApiResponse<String> adminLogin(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "어드민 로그인 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "email": "admin@test.com",
                          "password": "Password123!"
                        }
                        """
                )
            )
        )
        AuthDto.LoginRequest request, @Parameter(hidden = true) HttpServletResponse response);

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "토큰 갱신 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "성공", "data": {"accessToken": "eyJhbGciOiJIUzI1NiJ9..."}}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "유효하지 않은 리프레시 토큰",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "유효하지 않은 리프레시 토큰입니다", "errorCode": "UNAUTHORIZED"}""")))
    })
    ApiResponse<AuthDto.TokenResponse> refreshToken(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "리프레시 토큰",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                        }
                        """
                )
            )
        )
        AuthDto.TokenRefreshRequest request, @Parameter(hidden = true) HttpServletResponse response);

    @Operation(
        summary = "관리자 토큰 갱신",
        description = "쿠키에서 리프레시 토큰을 읽어와서 새로운 액세스 토큰을 쿠키로만 설정합니다. 요청 바디 없이 쿠키만으로 작동합니다.",
        security = @SecurityRequirement(name = "cookieAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "관리자 토큰 갱신 성공 - 새로운 액세스 토큰이 쿠키로만 설정됨",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "토큰이 갱신되었습니다"}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "리프레시 토큰 없음 또는 유효하지 않음",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "리프레시 토큰이 없습니다", "errorCode": "UNAUTHORIZED"}""")))
    })
    ApiResponse<String> adminRefreshToken(@Parameter(hidden = true) HttpServletRequest request, @Parameter(hidden = true) HttpServletResponse response);

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "비밀번호 변경 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "비밀번호가 변경되었습니다"}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "현재 비밀번호 불일치",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "현재 비밀번호가 일치하지 않습니다", "errorCode": "UNAUTHORIZED"}""")))
    })
    ApiResponse<String> changePassword(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "비밀번호 변경 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "currentPassword": "Password123!",
                          "newPassword": "NewPassword123!",
                          "newPasswordConfirm": "NewPassword123!"
                        }
                        """
                )
            )
        )
        AuthDto.PasswordChangeRequest request);

    @Operation(summary = "로그아웃", description = "현재 세션을 종료하고 토큰 쿠키를 삭제합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "로그아웃 성공 - 모든 토큰 쿠키 삭제",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "로그아웃되었습니다. 토큰이 삭제되었습니다."}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "인증이 필요합니다.", "errorCode": "UNAUTHORIZED"}""")))
    })
    ApiResponse<String> logout(@Parameter(hidden = true) @AuthenticationPrincipal User user, @Parameter(hidden = true) HttpServletResponse response);

    @Operation(summary = "이메일 중복 검사", description = "회원가입 전 이메일이 이미 사용 중인지 확인합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "이메일 중복 검사 완료",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "사용 가능한 이메일입니다", "data": {"available": true}}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "유효하지 않은 이메일 형식",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                            {"message": "유효한 이메일 형식이 아닙니다", "errorCode": "VALIDATION_FAILED"}""")))
    })
    ApiResponse<AuthDto.EmailCheckResponse> checkEmailAvailability(
        @Parameter(description = "확인할 이메일 주소", example = "test@test.com", required = true)
        @RequestParam("email") @Email(message = "유효한 이메일 형식이 아닙니다") @NotBlank(message = "이메일은 필수입니다") String email);
}
