package com.vitaltrip.vitaltrip.first_aid.controller;

import com.vitaltrip.vitaltrip.first_aid.controller.docs.FirstAidControllerDocs;
import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.auth.filter.FirstAidAuthenticationFilter;
import com.vitaltrip.vitaltrip.first_aid.dto.EmergencyChatAdviceRequest;
import com.vitaltrip.vitaltrip.first_aid.dto.EmergencyChatAdviceResponse;
import com.vitaltrip.vitaltrip.first_aid.service.FirstAidService;
import com.vitaltrip.vitaltrip.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/first-aid")
@RequiredArgsConstructor
public class FirstAidController implements FirstAidControllerDocs {

    private final FirstAidService firstAidService;

    @PostMapping("/advice")
    @Override
    public ApiResponse<EmergencyChatAdviceResponse> getEmergencyChatAdvice(
            @Valid @RequestBody EmergencyChatAdviceRequest request) {
        EmergencyChatAdviceResponse response = firstAidService.generateEmergencyAdvice(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/test")
    @Override
    public ApiResponse<Map<String, Object>> testAuthentication(@AuthenticationPrincipal Object principal) {
        Map<String, Object> responseData = new HashMap<>();

        if (principal instanceof User user) {
            responseData.put("userType", "AUTHENTICATED");
            responseData.put("description", "인증된 회원으로 접근했습니다.");

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("countryCode", user.getCountryCode());
            userInfo.put("phoneNumber", user.getPhoneNumber());

            responseData.put("userInfo", userInfo);

        } else if (principal instanceof FirstAidAuthenticationFilter.AnonymousFirstAidUser) {
            responseData.put("userType", "ANONYMOUS");
            responseData.put("description", "비회원으로 접근했습니다.");

        } else {
            responseData.put("userType", "UNKNOWN");
            responseData.put("description", "알 수 없는 사용자 타입입니다.");
        }

        return ApiResponse.success(responseData);
    }
}
