package com.vitaltrip.vitaltrip.presentation.user.docs;

import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.user.User;
import com.vitaltrip.vitaltrip.presentation.user.dto.ProfileUpdateRequest;
import com.vitaltrip.vitaltrip.presentation.user.dto.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "사용자 관리", description = "사용자 프로필 및 계정 관리 API")
public interface UserControllerDocs {

    @Operation(
            summary = "내 프로필 조회",
            description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
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
                                                "profileImageUrl": null,
                                                "provider": "LOCAL"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    ApiResponse<UserInfoResponse> getProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user);

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
            )
    })
    ApiResponse<String> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileUpdateRequest request);
}
