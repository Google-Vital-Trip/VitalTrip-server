package com.vitaltrip.vitaltrip.domain.auth.controller;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.domain.auth.service.AuthService;
import com.vitaltrip.vitaltrip.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "인증/인가", description = "회원가입, 로그인, 토큰 관리, 프로필 관리 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "회원가입",
        description = "새로운 사용자 계정을 생성합니다. 이메일, 비밀번호, 개인정보를 입력받아 계정을 생성합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthDto.AuthResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "성공"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패 또는 비밀번호 불일치",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "비밀번호 불일치",
                        value = """
                            {
                              "message": "비밀번호와 비밀번호 확인이 일치하지 않습니다",
                              "errorCode": "INVALID_REQUEST"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "유효성 검증 실패",
                        value = """
                            {
                              "message": "입력값 검증에 실패했습니다.",
                              "errorCode": "VALIDATION_FAILED"
                            }
                            """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "이메일 중복",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "이미 사용 중인 이메일입니다.",
                          "errorCode": "DUPLICATE_EMAIL"
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<AuthDto.AuthResponse> signUp(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "회원가입 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "email": "test@example.com",
                          "name": "홍길동",
                          "password": "Password123!",
                          "passwordConfirm": "Password123!",
                          "birthDate": "1990-01-01",
                          "countryCode": "KR",
                          "phoneNumber": "+821012345678"
                        }
                        """
                )
            )
        )
        AuthDto.SignUpRequest request) {
        authService.signUp(request);
        return ApiResponse.success("성공");
    }

    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthDto.AuthResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "user": {
                              "id": 1,
                              "email": "test@example.com",
                              "name": "홍길동",
                              "birthDate": "1990-01-01",
                              "countryCode": "KR",
                              "phoneNumber": "+821012345678",
                              "profileImageUrl": null
                            }
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "소셜 로그인 사용자의 일반 로그인 시도",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "소셜 로그인 사용자는 해당 방식으로 로그인해주세요",
                          "errorCode": "INVALID_REQUEST"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "비밀번호 불일치",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "비밀번호가 일치하지 않습니다",
                          "errorCode": "UNAUTHORIZED"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "등록되지 않은 이메일",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "등록되지 않은 이메일입니다",
                          "errorCode": "RESOURCE_NOT_FOUND"
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<AuthDto.AuthResponse> login(
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "로그인 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "email": "test@example.com",
                          "password": "Password123!"
                        }
                        """
                )
            )
        )
        AuthDto.LoginRequest request) {
        AuthDto.AuthResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "토큰 갱신",
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "토큰 갱신 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthDto.TokenResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 리프레시 토큰",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "유효하지 않은 리프레시 토큰입니다",
                          "errorCode": "UNAUTHORIZED"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "사용자를 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "사용자를 찾을 수 없습니다",
                          "errorCode": "RESOURCE_NOT_FOUND"
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<AuthDto.TokenResponse> refreshToken(
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
        AuthDto.TokenRefreshRequest request) {
        AuthDto.TokenResponse response = authService.refreshToken(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    @Operation(
        summary = "내 정보 조회",
        description = "현재 로그인한 사용자의 정보를 조회합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "내 정보 조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthDto.UserInfo.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "id": 1,
                            "email": "test@example.com",
                            "name": "홍길동",
                            "birthDate": "1990-01-01",
                            "countryCode": "KR",
                            "phoneNumber": "+821012345678",
                            "profileImageUrl": null
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "인증이 필요합니다.",
                          "errorCode": "UNAUTHORIZED"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "유효하지 않은 토큰",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "유효하지 않은 토큰입니다.",
                          "errorCode": "INVALID_TOKEN"
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<AuthDto.UserInfo> getMyInfo(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
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
    @Operation(
        summary = "프로필 수정",
        description = "사용자의 프로필 정보(이름, 생년월일, 국가코드, 전화번호)를 수정합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "프로필 수정 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "프로필이 업데이트되었습니다"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "입력값 검증에 실패했습니다.",
                          "errorCode": "VALIDATION_FAILED"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json")
        )
    })
    public ApiResponse<String> updateProfile(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "수정할 프로필 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "name": "김길동",
                          "birthDate": "1992-03-15",
                          "countryCode": "US",
                          "phoneNumber": "+12345678901"
                        }
                        """
                )
            )
        )
        AuthDto.ProfileUpdateRequest request) {

        authService.updateProfile(user, request);
        return ApiResponse.success("프로필이 업데이트되었습니다");
    }

    @PutMapping("/password")
    @Operation(
        summary = "비밀번호 변경",
        description = "현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다. 소셜 로그인 사용자는 사용할 수 없습니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "비밀번호 변경 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "비밀번호가 변경되었습니다"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "소셜 로그인 사용자 또는 새 비밀번호 불일치",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "소셜 로그인 사용자",
                        value = """
                            {
                              "message": "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다",
                              "errorCode": "INVALID_REQUEST"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "새 비밀번호 불일치",
                        value = """
                            {
                              "message": "새 비밀번호와 비밀번호 확인이 일치하지 않습니다",
                              "errorCode": "INVALID_REQUEST"
                            }
                            """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "현재 비밀번호 불일치",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "현재 비밀번호가 일치하지 않습니다",
                          "errorCode": "UNAUTHORIZED"
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<String> changePassword(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "비밀번호 변경 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "currentPassword": "OldPassword123!",
                          "newPassword": "NewPassword123!",
                          "newPasswordConfirm": "NewPassword123!"
                        }
                        """
                )
            )
        )
        AuthDto.PasswordChangeRequest request) {

        authService.changePassword(user, request);
        return ApiResponse.success("비밀번호가 변경되었습니다");
    }

    @PostMapping("/logout")
    @Operation(
        summary = "로그아웃",
        description = "현재 세션을 종료합니다. 클라이언트에서 토큰을 삭제해야 합니다.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "로그아웃되었습니다. 클라이언트에서 토큰을 삭제해주세요."
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json")
        )
    })
    public ApiResponse<String> logout(
        @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ApiResponse.success("로그아웃되었습니다. 클라이언트에서 토큰을 삭제해주세요.");
    }

    @GetMapping("/check-email")
    @Operation(
        summary = "이메일 중복 검사",
        description = "회원가입 전 이메일이 이미 사용 중인지 확인합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "이메일 중복 검사 완료",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "사용 가능한 이메일",
                        value = """
                            {
                              "message": "사용 가능한 이메일입니다",
                              "data": {
                                "available": true
                              }
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "이미 사용 중인 이메일",
                        value = """
                            {
                              "message": "이미 사용 중인 이메일입니다",
                              "data": {
                                "available": false
                              }
                            }
                            """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유효하지 않은 이메일 형식",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "유효한 이메일 형식이 아닙니다",
                          "errorCode": "INVALID_REQUEST"
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<AuthDto.EmailCheckResponse> checkEmailAvailability(
        @Parameter(
            description = "확인할 이메일 주소",
            example = "test@example.com",
            required = true
        )
        @RequestParam("email")
        @Email(message = "유효한 이메일 형식이 아닙니다")
        @NotBlank(message = "이메일은 필수입니다")
        String email) {

        AuthDto.EmailCheckResponse response = authService.checkEmailAvailability(email);

        if (response.available()) {
            return ApiResponse.success(response);
        } else {
            return ApiResponse.success(response);
        }
    }
}
