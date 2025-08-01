package com.vitaltrip.vitaltrip.domain.auth.controller;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.auth.dto.OAuthDto;
import com.vitaltrip.vitaltrip.domain.auth.service.OAuth2Service;
import com.vitaltrip.vitaltrip.domain.auth.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
@Tag(name = "OAuth2 소셜 로그인", description = "Google OAuth2 소셜 로그인 및 프로필 완성 API")
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;
    private final JwtUtil jwtUtil;

    @PostMapping("/complete-profile")
    @Operation(
        summary = "OAuth2 프로필 완성",
        description = """
            Google OAuth2로 가입한 사용자가 추가 프로필 정보를 입력하여 회원가입을 완료합니다.
            
            ## 프로세스
            1. Google OAuth2 인증 완료 후 임시 토큰 발급
            2. 프론트엔드에서 임시 토큰을 Authorization 헤더에 포함
            3. 추가 프로필 정보(생년월일, 국가코드, 전화번호) 입력
            4. 정식 액세스 토큰 및 리프레시 토큰 발급
            
            ## 주의사항
            - 임시 토큰은 30분간만 유효합니다
            - Authorization 헤더에 "Bearer {tempToken}" 형식으로 포함해야 합니다
            - 임시 토큰으로만 이 엔드포인트에 접근 가능합니다
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "프로필 완성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OAuthDto.CompleteProfileResponse.class),
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                            "user": {
                              "id": 2,
                              "email": "user@gmail.com",
                              "name": "홍길동",
                              "birthDate": "1990-01-01",
                              "countryCode": "KR",
                              "phoneNumber": "+821012345678",
                              "profileImageUrl": "https://lh3.googleusercontent.com/...",
                              "provider": "GOOGLE"
                            }
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패 또는 일반 회원가입 사용자 접근",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "유효성 검증 실패",
                        value = """
                            {
                              "message": "입력값 검증에 실패했습니다.",
                              "errorCode": "VALIDATION_FAILED"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "일반 회원가입 사용자",
                        value = """
                            {
                              "message": "일반 회원가입 사용자는 이 기능을 사용할 수 없습니다.",
                              "errorCode": "INVALID_REQUEST"
                            }
                            """
                    )
                }
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "유효하지 않은 임시 토큰",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "유효하지 않은 임시 토큰입니다.",
                          "errorCode": "INVALID_TEMP_TOKEN"
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
                          "message": "사용자를 찾을 수 없습니다.",
                          "errorCode": "USER_NOT_FOUND"
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<OAuthDto.CompleteProfileResponse> completeProfile(
        @Parameter(hidden = true) HttpServletRequest request,
        @Valid @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "완성할 프로필 정보",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                        {
                          "name": "홍길동",
                          "birthDate": "1990-01-01",
                          "countryCode": "KR",
                          "phoneNumber": "+821012345678"
                        }
                        """
                )
            )
        )
        OAuthDto.CompleteProfileRequest profileRequest) {

        String tempToken = extractTempToken(request);

        if (!jwtUtil.validateToken(tempToken) || !jwtUtil.isTempToken(tempToken)) {
            return ApiResponse.error("유효하지 않은 임시 토큰입니다.", "INVALID_TEMP_TOKEN");
        }

        OAuthDto.CompleteProfileResponse response = oauth2Service.completeProfile(tempToken,
            profileRequest);
        return ApiResponse.success(response);
    }

    @GetMapping("/login-url")
    @Operation(
        summary = "OAuth2 로그인 URL 조회",
        description = """
            Google OAuth2 로그인을 시작할 수 있는 URL을 반환합니다.
            
            ## 사용법
            1. 이 API를 호출하여 로그인 URL을 받습니다
            2. 반환된 `googleLoginUrl`로 리디렉션합니다
            3. 사용자가 Google에서 인증을 완료하면 콜백 URL로 리디렉션됩니다
            
            ## 콜백 처리
            - **프로필이 완성된 사용자**: `{frontend_url}?success=true&accessToken=...&refreshToken=...`
            - **프로필 완성이 필요한 사용자**: `{frontend_url}?needsProfile=true&tempToken=...&email=...&name=...`
            - **오류 발생**: `{frontend_url}?error=true&errorCode=...&message=...`
            
            ## 주의사항
            - 이 API는 인증이 필요하지 않습니다
            - 서버의 현재 호스트 정보를 기반으로 URL을 동적 생성합니다
            """
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "OAuth2 로그인 URL 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "googleLoginUrl": "http://dkswoalstest.duckdns.org:8080/oauth2/authorization/google",
                            "message": "위 URL로 이동하면 Google OAuth2 로그인이 시작됩니다."
                          }
                        }
                        """
                )
            )
        )
    })
    public ApiResponse<Object> getOAuth2LoginUrl(
        @Parameter(hidden = true) HttpServletRequest request) {

        String baseUrl =
            request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        return ApiResponse.success(new Object() {
            public String getGoogleLoginUrl() {
                return baseUrl + "/oauth2/authorization/google";
            }

            public String getMessage() {
                return "위 URL로 이동하면 Google OAuth2 로그인이 시작됩니다.";
            }
        });
    }

    private String extractTempToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        throw new IllegalArgumentException("Authorization 헤더에서 토큰을 찾을 수 없습니다.");
    }
}
