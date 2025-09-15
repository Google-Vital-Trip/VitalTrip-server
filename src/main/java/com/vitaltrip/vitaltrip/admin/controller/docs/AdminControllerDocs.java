package com.vitaltrip.vitaltrip.admin.controller.docs;

import com.vitaltrip.vitaltrip.admin.dto.UserPageResponse;
import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

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
            
            ## 🚨 보안 고려사항
            - 민감한 개인정보가 포함되므로 ADMIN 권한만 접근 가능
            - 비밀번호는 응답에서 완전히 제외됨
            - 모든 요청이 로깅됨
            """,
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "사용자 목록 조회 성공",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
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
                              },
                              {
                                "id": 2,
                                "email": "social@gmail.com",
                                "name": "김철수",
                                "birthDate": "1985-05-15",
                                "countryCode": "US",
                                "phoneNumber": "+12345678901",
                                "profileImageUrl": "https://lh3.googleusercontent.com/...",
                                "provider": "GOOGLE",
                                "providerId": "google123456",
                                "role": "USER",
                                "createdAt": "2024-01-02T00:00:00",
                                "updatedAt": "2024-01-02T00:00:00"
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
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 페이지네이션 파라미터",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "페이지 크기는 1에서 100 사이여야 합니다",
                          "errorCode": "VALIDATION_FAILED"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "인증 실패 - 토큰이 없거나 유효하지 않음",
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
            description = "권한 부족 - ADMIN 권한 필요",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "message": "접근 권한이 없습니다.",
                          "errorCode": "FORBIDDEN"
                        }
                        """
                )
            )
        )
    })
    ApiResponse<UserPageResponse> getAllUsers(
        @Parameter(
            description = "조회할 페이지 번호 (0부터 시작)",
            example = "0"
        )
        @Min(value = 0, message = "페이지는 0 이상이어야 합니다")
        int page,

        @Parameter(
            description = "한 페이지당 사용자 수 (1~100)",
            example = "20"
        )
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
        int size
    );
}
