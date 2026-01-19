package com.vitaltrip.vitaltrip.presentation.admin;

import com.vitaltrip.vitaltrip.presentation.admin.docs.AdminControllerDocs;
import com.vitaltrip.vitaltrip.presentation.admin.dto.AdminCheckResponse;
import com.vitaltrip.vitaltrip.presentation.admin.dto.UserPageResponse;
import com.vitaltrip.vitaltrip.application.admin.AdminService;
import com.vitaltrip.vitaltrip.presentation.health.dto.ApiResponse;
import com.vitaltrip.vitaltrip.domain.user.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController implements AdminControllerDocs {

    private final AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ApiResponse<UserPageResponse> getAllUsers(
        @RequestParam(defaultValue = "0")
        @Min(value = 0, message = "페이지는 0 이상이어야 합니다")
        int page,

        @RequestParam(defaultValue = "20")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
        @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
        int size
    ) {
        UserPageResponse response = adminService.getAllUsers(page, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<AdminCheckResponse> checkAdminStatus(@AuthenticationPrincipal User user) {
        boolean isAdmin = user != null && user.getRole() == User.Role.ADMIN;
        AdminCheckResponse response = new AdminCheckResponse(isAdmin);
        return ApiResponse.success(response);
    }
}
