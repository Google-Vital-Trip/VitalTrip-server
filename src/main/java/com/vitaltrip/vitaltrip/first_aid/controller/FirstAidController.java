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
        String content = "열원에서 즉시 떨어지세요\n" +
            "뜨거운 옷이나 장신구를 조심스럽게 제거하세요\n" +
            "찬물로 10-15분 동안 화상 부위를 식혀주세요\n" +
            "화상 부위에 직접 얼음을 사용하지 마세요\n" +
            "깨끗한 천으로 부위를 부드럽게 두드려 말리세요\n" +
            "멸균 거즈 붕대를 느슨하게 감아주세요\n" +
            "버터, 오일, 민간요법을 바르지 마세요\n" +
            "필요시 일반의약품 진통제를 복용하세요\n" +
            "심각한 화상이거나 확실하지 않다면 119에 신고하세요";

        String summary = "즉각적인 냉각과 적절한 화상 처치가 추가 손상을 방지하는 데 필수적입니다.";

        String recommendedAction = "즉시 119에 신고하고 찬물로 화상 부위를 식혀주세요";

        CountryIdentificationResponse identificationResponse = new CountryIdentificationResponse(
            "KR",
            "South Korea",
            37.5665,
            126.978,
            EmergencyContact.of("119", "112", "119", "112")
        );

        String disclaimer = "이는 AI의 임시 응급처치 조언입니다. 참고만 하시고 빠른 시간 내에 전문의에게 상담받으세요.";

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
