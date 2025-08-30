package com.vitaltrip.vitaltrip.first_aid.controller;

import com.vitaltrip.vitaltrip.first_aid.controller.docs.FirstAidControllerDocs;
import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import com.vitaltrip.vitaltrip.auth.filter.FirstAidAuthenticationFilter;
import com.vitaltrip.vitaltrip.first_aid.dto.EmergencyChatAdviceRequest;
import com.vitaltrip.vitaltrip.first_aid.dto.EmergencyChatAdviceResponse;
import com.vitaltrip.vitaltrip.first_aid.service.FirstAidService;
import com.vitaltrip.vitaltrip.location.dto.CountryIdentificationResponse;
import com.vitaltrip.vitaltrip.location.dto.EmergencyContact;
import com.vitaltrip.vitaltrip.user.domain.User;
import jakarta.validation.Valid;
import java.util.List;
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
        //EmergencyChatAdviceResponse response = firstAidService.generateEmergencyAdvice(request);

        try {
            Thread.sleep(3000); // 3초 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        EmergencyChatAdviceResponse response = createDemoResponse();
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

    public EmergencyChatAdviceResponse createDemoResponse() {
        String content = "Move away from the heat source immediately\n" +
            "Remove any hot clothing or jewelry carefully\n" +
            "Cool the burn with running cold water for 10-15 minutes\n" +
            "Do not use ice directly on the burn\n" +
            "Gently pat the area dry with a clean cloth\n" +
            "Apply a sterile gauze bandage loosely\n" +
            "Do not apply butter, oil, or home remedies\n" +
            "Take over-the-counter pain medication if needed\n" +
            "Call 119 for severe burns or if unsure";

        String summary = "Immediate cooling and proper burn care are essential to prevent further damage.";

        String recommendedAction = "Call 119 immediately and cool the burn with running water";

        CountryIdentificationResponse identificationResponse = new CountryIdentificationResponse(
            "KR",
            "South Korea",
            37.5665,
            126.978,
            EmergencyContact.of("119", "112", "119", "112")
        );

        String disclaimer = "This is temporary AI first aid advice. Please seek professional medical care immediately.";

        Integer confidence = 89;

        List<String> blogLinks = List.of(
            "https://www.119.go.kr/webapp/ptl/ptl010/ptl010_010100/ptl010_010100050/ptl010_010100050010/ptl010_010100050010.jsp",
            "https://www.redcross.or.kr/webapp/homepage/hp30100/hp30100.jsp?menuId=HP30101",
            "https://blog.naver.com/redcross_blog/221234567890"
        );

        return EmergencyChatAdviceResponse.of(content, summary, recommendedAction,
            identificationResponse, disclaimer, confidence, blogLinks);
    }

}
