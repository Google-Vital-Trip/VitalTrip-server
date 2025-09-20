package com.vitaltrip.vitaltrip.auth.controller.docs;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.auth.dto.AuthDto;
import com.vitaltrip.vitaltrip.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증/인가", description = "회원가입, 로그인, 토큰 관리, 프로필 관리 API")
public interface AuthControllerDocs {

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
    ApiResponse<AuthDto.AuthResponse> signUp(
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
            AuthDto.SignUpRequest request);

    @Operation(
            summary = "로그인",
            description = """
                    이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.
                    
                    ## 🍪 쿠키 설정
                    성공적인 로그인 시 다음 쿠키가 자동으로 설정됩니다:
                    - **accessToken**: 액세스 토큰 (1시간 유효, HttpOnly)
                    - **refreshToken**: 리프레시 토큰 (7일 유효, HttpOnly)
                    
                    ## 🔐 보안 설정
                    - **HttpOnly**: XSS 공격 방지를 위해 JavaScript에서 접근 불가
                    - **Secure**: HTTPS 환경에서만 전송 (프로덕션)
                    - **Path**: 전체 애플리케이션 경로에서 사용 가능
                    - **SameSite**: CSRF 공격 방지 (Lax 설정)
                    
                    클라이언트는 응답 바디의 토큰을 사용하거나, 자동으로 설정된 쿠키를 통해 인증할 수 있습니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 - 응답 바디에 토큰 포함 및 쿠키 자동 설정",
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
    ApiResponse<AuthDto.AuthResponse> login(
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
            AuthDto.LoginRequest request,

            @Parameter(hidden = true) HttpServletResponse response);

    @Operation(
            summary = "어드민 로그인",
            description = """
                    관리자 계정으로 로그인합니다. 일반 로그인과 유사하지만 다음과 같은 차이점이 있습니다:
                    
                    ## 🎯 어드민 로그인 특징
                    - **ADMIN 권한 필수**: USER 권한 계정으로는 로그인할 수 없습니다
                    - **토큰 쿠키 전용**: 응답 바디에는 토큰이 포함되지 않고 쿠키에만 설정됩니다
                    - **보안 강화**: 관리자 전용 세션 관리를 위한 별도 엔드포인트
                    
                    ## 🍪 쿠키 설정
                    성공적인 어드민 로그인 시 다음 쿠키가 자동으로 설정됩니다:
                    - **accessToken**: 액세스 토큰 (1시간 유효, HttpOnly)
                    - **refreshToken**: 리프레시 토큰 (7일 유효, HttpOnly)
                    
                    ## 🔐 보안 설정
                    - **HttpOnly**: XSS 공격 방지를 위해 JavaScript에서 접근 불가
                    - **Secure**: HTTPS 환경에서만 전송 (프로덕션)
                    - **Path**: 전체 애플리케이션 경로에서 사용 가능
                    - **Domain**: 설정된 도메인에서만 유효
                    
                    ## 💡 사용 시나리오
                    - 관리자 대시보드 로그인
                    - 백오피스 시스템 접근
                    - 시스템 관리 작업 시 사용
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "어드민 로그인 성공 - 토큰은 쿠키에만 설정됨",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "어드민 로그인이 완료되었습니다"
                                            }
                                            """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "소셜 로그인 사용자의 어드민 로그인 시도",
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
                    responseCode = "403",
                    description = "ADMIN 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "관리자 권한이 필요합니다",
                                              "errorCode": "FORBIDDEN"
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
    ApiResponse<String> adminLogin(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "어드민 로그인 정보 (일반 로그인과 동일한 형식)",
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
            AuthDto.LoginRequest request,

            @Parameter(hidden = true) HttpServletResponse response);


    @Operation(
            summary = "토큰 갱신",
            description = """
                    리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.
                    
                    ## 🍪 쿠키 업데이트
                    성공적인 토큰 갱신 시:
                    - **accessToken** 쿠키가 새로운 토큰으로 업데이트됩니다
                    - **refreshToken** 쿠키는 그대로 유지됩니다
                    
                    클라이언트는 응답 바디의 새로운 액세스 토큰을 사용하거나, 자동으로 업데이트된 쿠키를 사용할 수 있습니다.
                    """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공 - 새로운 액세스 토큰 쿠키 설정",
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
            AuthDto.TokenRefreshRequest request,

            @Parameter(hidden = true) HttpServletResponse response);

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
    ApiResponse<String> changePassword(
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
            AuthDto.PasswordChangeRequest request);

    @Operation(
            summary = "로그아웃",
            description = """
                    현재 세션을 종료하고 토큰을 무효화합니다.
                    
                    ## 🍪 쿠키 삭제
                    로그아웃 시 다음과 같이 처리됩니다:
                    - **accessToken** 쿠키 삭제
                    - **refreshToken** 쿠키 삭제
                    - 모든 토큰 쿠키의 만료시간을 0으로 설정하여 즉시 삭제
                    
                    클라이언트에서는 별도로 토큰을 삭제할 필요가 없습니다.
                    """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공 - 모든 토큰 쿠키 삭제",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "로그아웃되었습니다. 토큰이 삭제되었습니다."
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
            )
    })
    ApiResponse<String> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(hidden = true) HttpServletResponse response);

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
                                              "errorCode": "VALIDATION_FAILED"
                                            }
                                            """
                            )
                    )
            )
    })
    ApiResponse<AuthDto.EmailCheckResponse> checkEmailAvailability(
            @Parameter(
                    description = "확인할 이메일 주소",
                    example = "test@example.com",
                    required = true
            )
            @RequestParam("email")
            @Email(message = "유효한 이메일 형식이 아닙니다")
            @NotBlank(message = "이메일은 필수입니다")
            String email);
}
