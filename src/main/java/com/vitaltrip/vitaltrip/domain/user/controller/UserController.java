package com.vitaltrip.vitaltrip.domain.user.controller;

import com.vitaltrip.vitaltrip.domain.user.docs.UserControllerDocs;
import com.vitaltrip.vitaltrip.global.response.ApiResponse;
import com.vitaltrip.vitaltrip.domain.user.entity.User;
import com.vitaltrip.vitaltrip.domain.user.dto.ProfileUpdateRequest;
import com.vitaltrip.vitaltrip.domain.user.dto.UserInfoResponse;
import com.vitaltrip.vitaltrip.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    @GetMapping("/profile")
    @Override
    public ApiResponse<UserInfoResponse> getProfile(@AuthenticationPrincipal User user) {
        UserInfoResponse userInfo = userService.getUserInfo(user);
        return ApiResponse.success(userInfo);
    }

    @PutMapping("/profile")
    @Override
    public ApiResponse<String> updateProfile(@AuthenticationPrincipal User user,
                                             @Valid @RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(user, request);
        return ApiResponse.success("프로필이 업데이트되었습니다");
    }
}
