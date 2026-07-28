package com.vitaltrip.vitaltrip.presentation.admin.docs;

import com.vitaltrip.vitaltrip.presentation.admin.dto.AdminCheckResponse;
import com.vitaltrip.vitaltrip.presentation.admin.dto.UserPageResponse;
import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "관리자", description = "관리자 전용 API")
public interface AdminControllerDocs {

    @Operation(
        summary = "전체 사용자 목록 조회",
        description = """
            관리자 권한으로 모든 사용자 목록을 페이지네이션으로 조회합니다.
            
            ## 🔐 권한 요구사항
            - **ADMIN 권한 필수**: USER 권한으로는 접근할 수 없습니다
            - Authorization 헤더에 유효한 JWT 토큰 필요
            
            ## 📊 페이지네이션 파라미터
            - **page**: 조회할 페이지 번호 (0부터 시작, 기본값: 0)
            - **size**: 한 페이지당 사용자 수 (1~100, 기본값: 20)
            
            ## 📋 반환 데이터
            - 사용자의 모든 정보 (비밀번호 제외)
            - 계정 생성일/수정일 포함
            - 로그인 제공자 정보 (LOCAL/GOOGLE)
            - 사용자 역할 (USER/ADMIN)
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "사용자 목록 조회 성공",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {
                  "message": "성공",
                  "data": {
                    "content": [
                      {
                        "id": 1,
                        "email": "user1@example.com",
                        "name": "홍길동",
                        "birthDate": "1990-01-01",
                        "countryCode": "KR",
                        "phoneNumber": "+821012345678",
                        "profileImageUrl": null,
                        "provider": "LOCAL",
                        "providerId": null,
                        "role": "USER",
                        "createdAt": "2024-01-01T00:00:00",
                        "updatedAt": "2024-01-01T00:00:00"
                      }
                    ],
                    "page": 0,
                    "size": 20,
                    "totalElements": 150,
                    "totalPages": 8,
                    "first": true,
                    "last": false,
                    "hasContent": true,
                    "hasNext": true,
                    "hasPrevious": false
                  }
                }"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "잘못된 페이지네이션 파라미터",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"message": "페이지 크기는 1에서 100 사이여야 합니다", "errorCode": "VALIDATION_FAILED"}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "인증 실패",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"message": "인증이 필요합니다.", "errorCode": "UNAUTHORIZED"}"""))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403", description = "권한 부족 - ADMIN 권한 필요",
            content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                {"message": "접근 권한이 없습니다.", "errorCode": "FORBIDDEN"}""")))
    })
    ApiResponse<UserPageResponse> getAllUsers(
        @Parameter(description = "조회할 페이지 번호 (0부터 시작)", example = "0")
        @Min(value = 0, message = "페이지는 0 이상이어야 합니다") int page,

        @Parameter(description = "한 페이지당 사용자 수 (1~100)", example = "20")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다") int size
    );

    @Operation(
        summary = "관리자 권한 확인",
        description = """
            현재 인증된 사용자가 관리자 권한을 가지고 있는지 확인합니다.
            
            ## 🎯 사용 목적
            - 프론트엔드에서 관리자 UI 표시 여부 결정
            - 관리자 기능 접근 권한 사전 확인
            - 사용자 역할 기반 라우팅 처리
            
            ## 🔐 인증 요구사항
            - 유효한 JWT 토큰 필요 (헤더 또는 쿠키)
            - 토큰이 없거나 유효하지 않으면 false 반환
            
            ## 💡 반환값
            - **true**: 현재 사용자가 ADMIN 권한을 가진 경우
            - **false**: USER 권한이거나 인증되지 않은 경우
            
            ## 🚀 활용 예시
            ```javascript
            // 관리자 페이지 접근 전 권한 확인
            const response = await fetch('/api/admin/me', {
                credentials: 'include'
            });
            const data = await response.json();
            
            if (data.data.isAdmin) {
                // 관리자 대시보드 표시
                showAdminDashboard();
            } else {
                // 일반 사용자 페이지로 리다이렉트
                redirectToUserPage();
            }
            ```
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "관리자 권한 확인 완료",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(
                    name = "관리자 권한 있음",
                    summary = "ADMIN 권한을 가진 사용자",
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "isAdmin": true
                          }
                        }"""
                ),
                @ExampleObject(
                    name = "관리자 권한 없음",
                    summary = "USER 권한이거나 인증되지 않은 사용자",
                    value = """
                        {
                          "message": "성공",
                          "data": {
                            "isAdmin": false
                          }
                        }"""
                )
            }))
    })
    ApiResponse<AdminCheckResponse> checkAdminStatus(
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    );
}
